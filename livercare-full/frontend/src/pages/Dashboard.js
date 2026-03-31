import React, { useState, useEffect } from 'react';
import { getPatientDashboard } from '../api/client';
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, ReferenceLine
} from 'recharts';

export default function Dashboard({ patientCode }) {
  const [data, setData]     = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]   = useState(null);

  useEffect(() => {
    setLoading(true);
    getPatientDashboard(patientCode)
      .then(r => { setData(r.data.data); setLoading(false); })
      .catch(e => { setError(e.message); setLoading(false); });
  }, [patientCode]);

  if (loading) return <div className="loading">Loading patient data...</div>;
  if (error)   return <div className="loading" style={{color:'var(--danger)'}}>Error: {error}</div>;
  if (!data)   return null;

  const { patient, latestLab, latestDrift, unresolvedAlerts, recentLabs, baselineStats } = data;

  // Build chart data from recentLabs
  const chartData = (recentLabs || []).slice().reverse().map((lab, i) => ({
    day: lab.labDate,
    bilirubin: lab.bilirubin,
    inr: lab.inr,
    alt: lab.alt,
    baseline: baselineStats?.bilirubinMean || 0.8,
  }));

  const risk = patient.currentRiskLevel;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>

      {/* ── Header ────────────────────────────── */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <div className="page-title">Liver Drift Analysis</div>
          <div className="page-sub">
            {patient.fullName} · {patient.patientCode} · {patient.diagnosis}
            {latestLab && ` · ${latestLab.treatmentCycle || 'Active Monitoring'}`}
          </div>
        </div>
        <span className={`risk-pill risk-${risk}`}>
          {risk === 'HIGH' ? '⚠ ' : risk === 'MODERATE' ? '● ' : '✓ '}{risk} RISK
        </span>
      </div>

      {/* ── Bedrock Alert Banner ──────────────── */}
      {unresolvedAlerts?.length > 0 && unresolvedAlerts[0].bedrockNarrative && (
        <div className="alert-banner">
          <div className="alert-banner-icon">⚠️</div>
          <div className="alert-banner-body">
            <div className="alert-banner-title">
              Drift Alert · Score {unresolvedAlerts[0].driftScore}/100
              {unresolvedAlerts[0].daysAheadThreshold &&
                ` · ${unresolvedAlerts[0].daysAheadThreshold} Days Early Warning`}
            </div>
            <div className="alert-banner-msg">
              {unresolvedAlerts[0].flaggedMarkers?.join(' · ')} elevated above personal baseline
            </div>
            <div className="bedrock-narrative">
              <div className="bedrock-narrative-label">⚡ Amazon Bedrock · Claude 3 Sonnet</div>
              <div className="bedrock-narrative-text">{unresolvedAlerts[0].bedrockNarrative}</div>
            </div>
          </div>
        </div>
      )}

      {/* ── Stats Row ─────────────────────────── */}
      {latestLab && (
        <div className="stats-row">
          <StatCard
            label="Drift Score"
            value={patient.driftScore}
            unit="/100"
            color={risk === 'HIGH' ? 'danger' : risk === 'MODERATE' ? 'warn' : 'accent'}
            delta={`${risk} Risk`}
          />
          <StatCard
            label="Bilirubin"
            value={latestLab.bilirubin?.toFixed(1)}
            unit="mg/dL"
            color="warn"
            delta={baselineStats ? `Baseline: ${baselineStats.bilirubinMean?.toFixed(1)}` : ''}
          />
          <StatCard
            label="INR"
            value={latestLab.inr?.toFixed(2)}
            unit="ratio"
            color="warn"
            delta={baselineStats ? `Baseline: ${baselineStats.inrMean?.toFixed(2)}` : ''}
          />
          <StatCard
            label="Albumin"
            value={latestLab.albumin?.toFixed(1) || '—'}
            unit="g/dL"
            color="accent"
            delta={baselineStats ? `Baseline: ${baselineStats.albuminMean?.toFixed(1)}` : ''}
          />
        </div>
      )}

      {/* ── Chart + Score ─────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 300px', gap: 20 }}>

        {/* Trend chart */}
        <div className="panel">
          <div className="panel-header">
            <div className="panel-title">🔬 Biomarker Trend</div>
            <div className="panel-meta">Personal baseline zone shown</div>
          </div>
          <div style={{ padding: '20px 20px 10px' }}>
            <ResponsiveContainer width="100%" height={220}>
              <LineChart data={chartData} margin={{ top: 5, right: 10, left: -20, bottom: 5 }}>
                <CartesianGrid stroke="rgba(255,255,255,0.05)" />
                <XAxis dataKey="day" tick={{ fill: '#6b7a8d', fontSize: 10, fontFamily: 'DM Mono' }} />
                <YAxis tick={{ fill: '#6b7a8d', fontSize: 10, fontFamily: 'DM Mono' }} />
                <Tooltip
                  contentStyle={{ background: '#0d1117', border: '1px solid rgba(255,255,255,0.1)', borderRadius: 8 }}
                  labelStyle={{ color: '#6b7a8d', fontFamily: 'DM Mono', fontSize: 11 }}
                  itemStyle={{ fontFamily: 'DM Mono', fontSize: 11 }}
                />
                <ReferenceLine
                  y={baselineStats?.bilirubinMean || 0.8}
                  stroke="rgba(0,200,150,0.4)"
                  strokeDasharray="4 4"
                  label={{ value: 'Baseline', fill: 'rgba(0,200,150,0.6)', fontSize: 9, fontFamily: 'DM Mono' }}
                />
                <Line type="monotone" dataKey="bilirubin" stroke="#f5a623" strokeWidth={2} dot={false} name="Bilirubin" />
                <Line type="monotone" dataKey="inr"       stroke="#ff4757" strokeWidth={2} dot={false} name="INR" />
              </LineChart>
            </ResponsiveContainer>
          </div>

          {/* Biomarker bars */}
          {latestLab && baselineStats && (
            <div style={{ padding: '0 20px 20px', display: 'flex', flexDirection: 'column', gap: 12 }}>
              {[
                { name: 'Bilirubin', val: latestLab.bilirubin, base: baselineStats.bilirubinMean, unit: 'mg/dL' },
                { name: 'INR',       val: latestLab.inr,       base: baselineStats.inrMean,       unit: '' },
                { name: 'ALT',       val: latestLab.alt,       base: baselineStats.altMean,       unit: 'U/L' },
                { name: 'AST',       val: latestLab.ast,       base: baselineStats.astMean,       unit: 'U/L' },
              ].map(m => {
                const pct = base => base > 0 ? Math.min(100, Math.abs((m.val - m.base) / m.base) * 200) : 0;
                const drift = m.base > 0 ? ((m.val - m.base) / m.base * 100) : 0;
                const barColor = drift > 30 ? 'bar-danger' : drift > 15 ? 'bar-warn' : 'bar-accent';
                return (
                  <div key={m.name} style={{ display: 'grid', gridTemplateColumns: '90px 1fr 70px 60px', alignItems: 'center', gap: 12 }}>
                    <div>
                      <div style={{ fontSize: 13, fontWeight: 600 }}>{m.name}</div>
                      <div style={{ fontFamily: 'DM Mono', fontSize: 10, color: 'var(--muted)' }}>{m.unit}</div>
                    </div>
                    <div className="bio-bar-track">
                      <div className={`bio-bar-fill ${barColor}`} style={{ width: `${pct()}%` }} />
                    </div>
                    <div style={{ fontFamily: 'DM Mono', fontSize: 13, textAlign: 'right',
                      color: drift > 30 ? 'var(--danger)' : drift > 15 ? 'var(--warn)' : 'var(--text)' }}>
                      {m.val?.toFixed(1)}
                    </div>
                    <div style={{ fontFamily: 'DM Mono', fontSize: 11, textAlign: 'right',
                      color: drift > 0 ? 'var(--danger)' : 'var(--accent)' }}>
                      {drift > 0 ? '▲' : '▼'} {Math.abs(drift).toFixed(0)}%
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Drift score ring + markers */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}>
            <div style={{ fontFamily: 'DM Mono', fontSize: 9, textTransform: 'uppercase', letterSpacing: '1.5px', color: 'var(--muted)', marginBottom: 16 }}>Drift Score</div>
            <ScoreRing score={patient.driftScore} risk={risk} />
            {latestDrift?.flaggedMarkers?.length > 0 && (
              <div style={{ marginTop: 16, width: '100%' }}>
                <div style={{ fontFamily: 'DM Mono', fontSize: 9, textTransform: 'uppercase', letterSpacing: 1, color: 'var(--muted)', marginBottom: 8 }}>Contributing Markers</div>
                {Object.entries(latestDrift.markerWeights || {}).map(([k, v]) => (
                  <div key={k} style={{
                    display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                    background: 'var(--surface2)', border: '1px solid var(--border)',
                    borderRadius: 8, padding: '7px 12px', marginBottom: 6, fontSize: 12
                  }}>
                    <span style={{ fontWeight: 600 }}>{k.charAt(0).toUpperCase() + k.slice(1)}</span>
                    <span style={{ fontFamily: 'DM Mono', color: 'var(--danger)' }}>{Math.round(v * 100)}%</span>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Baseline info */}
          {baselineStats && (
            <div className="card">
              <div style={{ fontFamily: 'DM Mono', fontSize: 9, textTransform: 'uppercase', letterSpacing: 1, color: 'var(--muted)', marginBottom: 12 }}>Personal Baseline</div>
              {[
                ['Bilirubin', `${baselineStats.bilirubinMean?.toFixed(2)} ± ${baselineStats.bilirubinStd?.toFixed(2)} mg/dL`],
                ['INR',       `${baselineStats.inrMean?.toFixed(2)} ± ${baselineStats.inrStd?.toFixed(2)}`],
                ['ALT',       `${baselineStats.altMean?.toFixed(0)} ± ${baselineStats.altStd?.toFixed(0)} U/L`],
                ['Samples',   `${baselineStats.sampleSize}`],
              ].map(([k, v]) => (
                <div key={k} style={{ display: 'flex', justifyContent: 'space-between', padding: '6px 0', borderBottom: '1px solid var(--border)', fontSize: 12 }}>
                  <span style={{ color: 'var(--muted)' }}>{k}</span>
                  <span style={{ fontFamily: 'DM Mono' }}>{v}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* ── Recent Labs table ──────────────────── */}
      {recentLabs?.length > 0 && (
        <div className="panel">
          <div className="panel-header">
            <div className="panel-title">📋 Recent Lab Results</div>
            <div className="panel-meta">Latest {recentLabs.length} readings</div>
          </div>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Date</th><th>Bilirubin</th><th>INR</th><th>ALT</th><th>AST</th><th>Albumin</th><th>Cycle</th>
                </tr>
              </thead>
              <tbody>
                {recentLabs.map(lab => (
                  <tr key={lab.id}>
                    <td style={{ fontFamily: 'DM Mono', color: 'var(--muted)' }}>{lab.labDate}</td>
                    <td style={{ fontFamily: 'DM Mono' }}>{lab.bilirubin?.toFixed(1)}</td>
                    <td style={{ fontFamily: 'DM Mono' }}>{lab.inr?.toFixed(2)}</td>
                    <td style={{ fontFamily: 'DM Mono' }}>{lab.alt?.toFixed(0)}</td>
                    <td style={{ fontFamily: 'DM Mono' }}>{lab.ast?.toFixed(0)}</td>
                    <td style={{ fontFamily: 'DM Mono' }}>{lab.albumin?.toFixed(1) || '—'}</td>
                    <td style={{ fontFamily: 'DM Mono', fontSize: 11, color: 'var(--muted)' }}>{lab.treatmentCycle || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

function StatCard({ label, value, unit, color, delta }) {
  return (
    <div className={`stat-card ${color}`}>
      <div className="stat-label">{label}</div>
      <div className={`stat-value ${color}`}>{value}<span style={{ fontSize: 12, marginLeft: 4 }}>{unit}</span></div>
      {delta && <div className="stat-delta" style={{ color: color === 'accent' ? 'var(--muted)' : `var(--${color})` }}>{delta}</div>}
    </div>
  );
}

function ScoreRing({ score, risk }) {
  const color = risk === 'HIGH' ? '#ff4757' : risk === 'MODERATE' ? '#f5a623' : '#00c896';
  const r = 46, circ = 2 * Math.PI * r;
  const fill = (score / 100) * circ;
  return (
    <div className="score-ring-wrap">
      <svg width="120" height="120" viewBox="0 0 120 120">
        <circle cx="60" cy="60" r={r} fill="none" stroke="rgba(255,255,255,0.07)" strokeWidth="10" />
        <circle cx="60" cy="60" r={r} fill="none" stroke={color} strokeWidth="10"
          strokeDasharray={`${fill} ${circ}`} strokeLinecap="round"
          style={{ filter: `drop-shadow(0 0 8px ${color}80)` }} />
      </svg>
      <div className="score-ring-center">
        <div className="score-ring-num" style={{ color }}>{score}</div>
        <div className="score-ring-denom">/100</div>
      </div>
    </div>
  );
}
