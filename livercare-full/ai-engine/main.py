"""
LiverCare AI Drift Engine
FastAPI + scikit-learn Isolation Forest

Endpoints:
  POST /analyze   — full drift analysis
  GET  /health    — health check
  GET  /docs      — Swagger UI
"""

import os
import json
import math
import pickle
import logging
from typing import Optional

import numpy as np
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("livercare-ai")

app = FastAPI(
    title="LiverCare AI Drift Engine",
    description="Isolation Forest + Z-score drift detection for liver biomarkers",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# ── Marker weights (Bilirubin, INR, ALT, AST, Albumin) ──
WEIGHTS = {
    "bilirubin": 0.35,
    "inr":       0.25,
    "alt":       0.20,
    "ast":       0.12,
    "albumin":   0.08,
}

# ── Pydantic models ──────────────────────────────────────

class BaselineStats(BaseModel):
    alt_mean: float; alt_std: float
    ast_mean: float; ast_std: float
    bilirubin_mean: float; bilirubin_std: float
    inr_mean: float; inr_std: float
    albumin_mean: float; albumin_std: float
    sample_size: int

class DriftRequest(BaseModel):
    patient_id: int
    alt: float       = Field(..., gt=0)
    ast: float       = Field(..., gt=0)
    bilirubin: float = Field(..., gt=0)
    inr: float       = Field(..., gt=0)
    albumin: Optional[float] = None
    treatment_cycle: Optional[str] = None
    baseline_stats: BaselineStats

class DriftResult(BaseModel):
    drift_score: int
    risk_level: str
    marker_weights: dict
    flagged_markers: list[str]
    explanation: str
    days_ahead_of_threshold: Optional[int]


# ── Core detection logic ─────────────────────────────────

def sigmoid(z: float) -> float:
    return 1.0 / (1.0 + math.exp(-z))

def z_score(value: float, mean: float, std: float) -> float:
    return (value - mean) / std if std > 0 else 0.0

def isolation_forest_score(features: np.ndarray) -> float:
    """
    Train a small Isolation Forest on the single observation
    relative to typical normal-range distributions.
    In production this model would be pre-trained per patient.
    """
    # Generate synthetic normal population around baseline
    rng = np.random.default_rng(42)
    n_normal = 200
    normal_data = rng.normal(loc=0.0, scale=1.0, size=(n_normal, features.shape[0]))
    train_data = np.vstack([normal_data, features.reshape(1, -1)])

    iso = IsolationForest(n_estimators=100, contamination=0.05, random_state=42)
    iso.fit(train_data)
    raw_score = iso.score_samples(features.reshape(1, -1))[0]  # negative = anomalous
    # Normalise: typical range [-0.5, 0.2] → map to [0, 1]
    normalised = (raw_score + 0.5) / 0.7
    normalised = max(0.0, min(1.0, 1.0 - normalised))  # invert so 1 = anomalous
    return normalised


def analyse_drift(req: DriftRequest) -> DriftResult:
    bs = req.baseline_stats
    albumin = req.albumin if req.albumin is not None else bs.albumin_mean

    # Z-scores per marker
    z = {
        "bilirubin": z_score(req.bilirubin, bs.bilirubin_mean, bs.bilirubin_std),
        "inr":       z_score(req.inr,       bs.inr_mean,       bs.inr_std),
        "alt":       z_score(req.alt,       bs.alt_mean,       bs.alt_std),
        "ast":       z_score(req.ast,       bs.ast_mean,       bs.ast_std),
        "albumin":   z_score(albumin,        bs.albumin_mean,   bs.albumin_std),
    }

    # Z-normalised feature vector for Isolation Forest
    features = np.array([z["bilirubin"], z["inr"], z["alt"], z["ast"], z["albumin"]])
    iso_score = isolation_forest_score(features)

    # Weighted sigmoid score
    weighted = (
        WEIGHTS["bilirubin"] * sigmoid(z["bilirubin"]) +
        WEIGHTS["inr"]       * sigmoid(z["inr"]) +
        WEIGHTS["alt"]       * sigmoid(z["alt"]) +
        WEIGHTS["ast"]       * sigmoid(z["ast"]) +
        WEIGHTS["albumin"]   * sigmoid(-z["albumin"])  # albumin: lower = worse
    )

    # Blend: 60% weighted z-score, 40% Isolation Forest
    combined = 0.60 * weighted + 0.40 * iso_score
    score = int(min(100, max(0, combined * 100)))

    # Risk tier
    risk = "HIGH" if score >= 60 else "MODERATE" if score >= 35 else "LOW"

    # Flagged markers (z > 1.5 or albumin z < -1.5)
    flagged = [
        m for m, zv in z.items()
        if (m != "albumin" and zv > 1.5) or (m == "albumin" and zv < -1.5)
    ]

    # Pct changes for explanation
    bil_pct = ((req.bilirubin - bs.bilirubin_mean) / bs.bilirubin_mean * 100) if bs.bilirubin_mean else 0
    inr_pct = ((req.inr       - bs.inr_mean)       / bs.inr_mean       * 100) if bs.inr_mean else 0

    # Days-ahead estimate
    days_ahead = None
    if score >= 70:   days_ahead = 7
    elif score >= 55: days_ahead = 10
    elif score >= 35: days_ahead = 14

    explanation = (
        f"Drift score {score}/100 — {risk} risk. "
        f"Bilirubin {bil_pct:+.0f}% from personal baseline ({req.bilirubin:.1f} vs {bs.bilirubin_mean:.1f} mg/dL). "
        f"INR {inr_pct:+.0f}% ({req.inr:.2f} vs {bs.inr_mean:.2f}). "
        f"Isolation Forest anomaly signal: {iso_score:.2f}. "
        f"Flagged markers: {', '.join(flagged) if flagged else 'none'}."
    )
    if days_ahead:
        explanation += f" Estimated {days_ahead} days ahead of threshold breach."

    log.info(
        "patient=%s score=%d risk=%s flagged=%s",
        req.patient_id, score, risk, flagged
    )

    return DriftResult(
        drift_score=score,
        risk_level=risk,
        marker_weights=WEIGHTS,
        flagged_markers=flagged,
        explanation=explanation,
        days_ahead_of_threshold=days_ahead,
    )


# ── Routes ───────────────────────────────────────────────

@app.post("/analyze", response_model=DriftResult, tags=["Drift"])
async def analyze(req: DriftRequest):
    """
    Primary endpoint: receive a lab result + baseline stats,
    return drift score, risk level, and explanation.
    """
    try:
        return analyse_drift(req)
    except Exception as e:
        log.error("Analysis failed: %s", str(e))
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/health", tags=["System"])
async def health():
    return {"status": "ok", "service": "livercare-ai-engine", "version": "1.0.0"}


@app.get("/", include_in_schema=False)
async def root():
    return {"message": "LiverCare AI Engine. Visit /docs for API."}
