"""
pytest tests for the LiverCare AI Drift Engine
Run: pytest tests/test_drift.py -v
"""
import pytest
from fastapi.testclient import TestClient
import sys, os
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))
from main import app

client = TestClient(app)

BASELINE = {
    "alt_mean": 33.0, "alt_std": 1.5,
    "ast_mean": 29.0, "ast_std": 1.2,
    "bilirubin_mean": 0.82, "bilirubin_std": 0.02,
    "inr_mean": 1.01, "inr_std": 0.02,
    "albumin_mean": 3.79, "albumin_std": 0.03,
    "sample_size": 6
}

def test_health():
    r = client.get("/health")
    assert r.status_code == 200
    assert r.json()["status"] == "ok"

def test_high_risk_detection():
    """Demo patient values should yield HIGH risk"""
    payload = {
        "patient_id": 1,
        "alt": 67.0, "ast": 54.0,
        "bilirubin": 2.80, "inr": 1.60, "albumin": 3.10,
        "treatment_cycle": "Sorafenib Cycle 4",
        "baseline_stats": BASELINE
    }
    r = client.post("/analyze", json=payload)
    assert r.status_code == 200
    data = r.json()
    assert data["drift_score"] > 55
    assert data["risk_level"] in ("MODERATE", "HIGH")
    assert "bilirubin" in data["flagged_markers"]
    assert data["days_ahead_of_threshold"] is not None

def test_low_risk_stable_patient():
    """Stable patient values should yield LOW risk"""
    stable_baseline = {
        "alt_mean": 29.0, "alt_std": 1.0,
        "ast_mean": 25.0, "ast_std": 1.0,
        "bilirubin_mean": 0.71, "bilirubin_std": 0.02,
        "inr_mean": 0.99, "inr_std": 0.01,
        "albumin_mean": 3.98, "albumin_std": 0.03,
        "sample_size": 4
    }
    payload = {
        "patient_id": 2,
        "alt": 30.0, "ast": 26.0,
        "bilirubin": 0.74, "inr": 1.00, "albumin": 3.97,
        "baseline_stats": stable_baseline
    }
    r = client.post("/analyze", json=payload)
    assert r.status_code == 200
    data = r.json()
    assert data["drift_score"] < 30
    assert data["risk_level"] == "LOW"
    assert len(data["flagged_markers"]) == 0

def test_score_range():
    """Score must always be 0–100"""
    payload = {
        "patient_id": 99,
        "alt": 500.0, "ast": 400.0,
        "bilirubin": 15.0, "inr": 4.0, "albumin": 1.5,
        "baseline_stats": BASELINE
    }
    r = client.post("/analyze", json=payload)
    assert 0 <= r.json()["drift_score"] <= 100

def test_validation_rejects_negative():
    """Negative biomarker values must be rejected"""
    payload = {
        "patient_id": 1,
        "alt": -5.0, "ast": 30.0,
        "bilirubin": 1.0, "inr": 1.0,
        "baseline_stats": BASELINE
    }
    r = client.post("/analyze", json=payload)
    assert r.status_code == 422
