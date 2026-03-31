// ── AlertsPage ────────────────────────────────────────────
import React, { useState, useEffect } from 'react';
import { getAlerts, resolveAlert, listPatients, submitLab } from '../api/client';

export function AlertsPage() {
  const [alerts, setAlerts]   = useState([]);
  const [loading, setLoading] = useState(true);
  const [resolving, setResolving] = useState(null);

  const load = () => {
    setLoading(true);
    getAlerts().then(r => { setAlerts(r.data.data || []); setLoading(false); });
  };

  useEffect(load, []);

  const handleResolve = async (id) => {
    const by = prompt('Resolved by (clinician name):');
    const notes = prompt('Resolution notes:');
    if (!by) return;
    setResolving(id);
    await resolveAlert(id, { resolvedBy: by, resolutionNotes: notes || '' });
    setResolving(null);
    load();
  };

  if (loading) return <div className="loading">Loading alerts...</div>;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      <div>
        <div className="page-title">Drift Alerts</div>
        <div className="page-sub">{alerts.length} unresolved · Ordered by drift score</div>
      </div>

      {alerts.length === 0 && (
        <div className="empty">✅ No unresolved alerts</div>
      )}

      {alerts.map(alert => (
        <div key={alert.id} className="panel">
          <div className="panel-header" style={{ background: alert.riskLevel === 'HIGH' ? 'rgba(255,71,87,0.05)' : 'rgba(245,166,35,0.05)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <span className={`risk-pill risk-${alert.riskLevel}`}>{alert.riskLevel}</span>
              <div>
                <div style={{ fontWeight: 600 }}>{alert.patientName} · {alert.patientCode}</div>
                <div style={{ fontFamily: 'DM Mono', fontSize: 10, color: 'var(--muted)' }}>{new Date(alert.createdAt).toLocaleString()}</div>
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontFamily: 'DM Mono', fontSize: 22, fontWeight: 500,
                  color: alert.riskLevel === 'HIGH' ? 'var(--danger)' : 'var(--warn)' }}>{alert.driftScore}</div>
                <div style={{ fontFamily: 'DM Mono', fontSize: 9, color: 'var(--muted)' }}>DRIFT SCORE</div>
              </div>
              <button className="btn btn-ghost btn-sm" onClick={() => handleResolve(alert.id)} disabled={resolving === alert.id}>
                {resolving === alert.id ? 'Resolving...' : '✓ Resolve'}
              </button>
            </div>
          </div>

          <div style={{ padding: 20 }}>
            {/* Statistical explanation */}
            <div style={{ fontFamily: 'DM Mono', fontSize: 11, color: 'var(--muted)', marginBottom: 12, lineHeight: 1.6 }}>
              {alert.aiExplanation}
            </div>

            {/* Bedrock narrative */}
            {alert.bedrockNarrative && (
              <div className="bedrock-narrative">
                <div className="bedrock-narrative-label">⚡ Amazon Bedrock · Claude 3 Sonnet · Clinical Narrative</div>
                <div className="bedrock-narrative-text">{alert.bedrockNarrative}</div>
              </div>
            )}

            {/* Flagged markers */}
            {alert.flaggedMarkers?.length > 0 && (
              <div style={{ marginTop: 12, display: 'flex', gap: 8 }}>
                {alert.flaggedMarkers.map(m => (
                  <span key={m} style={{
                    fontFamily: 'DM Mono', fontSize: 10, padding: '2px 8px', borderRadius: 4,
                    background: 'rgba(255,71,87,0.12)', color: 'var(--danger)'
                  }}>{m}</span>
                ))}
              </div>
            )}
          </div>
        </div>
      ))}
    </div>
  );
}

export default AlertsPage;

// ── PatientList ───────────────────────────────────────────
export function PatientList({ onSelect }) {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading]   = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    fullName: '', dateOfBirth: '', gender: 'MALE',
    diagnosis: '', primaryTreatment: '', enrollmentDate: ''
  });

  useEffect(() => {
    listPatients().then(r => { setPatients(r.data.data || []); setLoading(false); });
  }, []);

  const handleRegister = async (e) => {
    e.preventDefault();
    const { default: api } = await import('../api/client');
    await api.post('/patients', form);
    setShowForm(false);
    listPatients().then(r => setPatients(r.data.data || []));
  };

  if (loading) return <div className="loading">Loading patients...</div>;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div className="page-title">Patients</div>
          <div className="page-sub">{patients.length} registered</div>
        </div>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>+ Register Patient</button>
      </div>

      {showForm && (
        <div className="panel">
          <div className="panel-header"><div className="panel-title">New Patient</div></div>
          <form onSubmit={handleRegister} style={{ padding: 20 }}>
            <div className="form-grid" style={{ marginBottom: 16 }}>
              {[
                { key: 'fullName', label: 'Full Name', type: 'text' },
                { key: 'dateOfBirth', label: 'Date of Birth', type: 'date' },
                { key: 'enrollmentDate', label: 'Enrollment Date', type: 'date' },
                { key: 'diagnosis', label: 'Diagnosis', type: 'text' },
                { key: 'primaryTreatment', label: 'Primary Treatment', type: 'text' },
              ].map(f => (
                <div key={f.key} className="form-group">
                  <label className="form-label">{f.label}</label>
                  <input className="form-input" type={f.type} value={form[f.key]}
                    onChange={e => setForm({...form, [f.key]: e.target.value})} required />
                </div>
              ))}
              <div className="form-group">
                <label className="form-label">Gender</label>
                <select className="form-input" value={form.gender} onChange={e => setForm({...form, gender: e.target.value})}>
                  <option>MALE</option><option>FEMALE</option><option>OTHER</option>
                </select>
              </div>
            </div>
            <div style={{ display: 'flex', gap: 10 }}>
              <button type="submit" className="btn btn-primary">Register</button>
              <button type="button" className="btn btn-ghost" onClick={() => setShowForm(false)}>Cancel</button>
            </div>
          </form>
        </div>
      )}

      <div className="panel">
        <table>
          <thead>
            <tr><th>Code</th><th>Name</th><th>Diagnosis</th><th>Treatment</th><th>Risk</th><th>Score</th><th></th></tr>
          </thead>
          <tbody>
            {patients.map(p => (
              <tr key={p.id}>
                <td style={{ fontFamily: 'DM Mono', fontSize: 11, color: 'var(--muted)' }}>{p.patientCode}</td>
                <td style={{ fontWeight: 600 }}>{p.fullName}</td>
                <td style={{ color: 'var(--muted)', fontSize: 12 }}>{p.diagnosis || '—'}</td>
                <td style={{ color: 'var(--muted)', fontSize: 12 }}>{p.primaryTreatment || '—'}</td>
                <td><span className={`risk-pill risk-${p.currentRiskLevel}`}>{p.currentRiskLevel}</span></td>
                <td style={{ fontFamily: 'DM Mono',
                  color: p.currentRiskLevel === 'HIGH' ? 'var(--danger)' : p.currentRiskLevel === 'MODERATE' ? 'var(--warn)' : 'var(--accent)' }}>
                  {p.driftScore}
                </td>
                <td>
                  <button className="btn btn-ghost btn-sm" onClick={() => onSelect(p.patientCode)}>Dashboard →</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// ── LabEntry ──────────────────────────────────────────────
export function LabEntry({ onSuccess }) {
  const [patients, setPatients] = useState([]);
  const [result, setResult]     = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({
    patientId: '', labDate: new Date().toISOString().split('T')[0],
    alt: '', ast: '', bilirubin: '', inr: '', albumin: '', treatmentCycle: ''
  });

  useEffect(() => {
    listPatients().then(r => setPatients(r.data.data || []));
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const r = await submitLab({
        ...form,
        patientId: Number(form.patientId),
        alt: Number(form.alt), ast: Number(form.ast),
        bilirubin: Number(form.bilirubin), inr: Number(form.inr),
        albumin: form.albumin ? Number(form.albumin) : undefined,
      });
      setResult(r.data.data);
    } catch (err) {
      alert('Error: ' + (err.response?.data?.message || err.message));
    }
    setSubmitting(false);
  };

  if (result) {
    const drift = result.driftAnalysis;
    return (
      <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
        <div className="page-title">Lab Submitted ✓</div>

        {drift && (
          <div className="panel">
            <div className="panel-header">
              <div className="panel-title">Drift Analysis Result</div>
              <span className={`risk-pill risk-${drift.riskLevel}`}>{drift.riskLevel}</span>
            </div>
            <div style={{ padding: 20 }}>
              <div style={{ fontFamily: 'DM Mono', marginBottom: 12, color: 'var(--muted)', fontSize: 12 }}>
                {drift.explanation}
              </div>
              {drift.daysAheadOfThreshold && (
                <div style={{ fontFamily: 'DM Mono', color: 'var(--warn)', fontSize: 13, marginBottom: 12 }}>
                  ⏱ Detected ~{drift.daysAheadOfThreshold} days before threshold breach
                </div>
              )}
            </div>
          </div>
        )}

        <div style={{ display: 'flex', gap: 10 }}>
          <button className="btn btn-primary" onClick={onSuccess}>Go to Dashboard</button>
          <button className="btn btn-ghost" onClick={() => setResult(null)}>Submit Another</button>
        </div>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      <div>
        <div className="page-title">Submit Lab Result</div>
        <div className="page-sub">Triggers drift analysis + Bedrock narrative automatically</div>
      </div>

      <div className="panel">
        <div className="panel-header"><div className="panel-title">🔬 New Lab Entry</div></div>
        <form onSubmit={handleSubmit} style={{ padding: 24 }}>
          <div className="form-grid" style={{ marginBottom: 20 }}>
            <div className="form-group" style={{ gridColumn: '1 / -1' }}>
              <label className="form-label">Patient</label>
              <select className="form-input" value={form.patientId} onChange={e => setForm({...form, patientId: e.target.value})} required>
                <option value="">Select patient...</option>
                {patients.map(p => <option key={p.id} value={p.id}>{p.fullName} · {p.patientCode}</option>)}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Lab Date</label>
              <input className="form-input" type="date" value={form.labDate} onChange={e => setForm({...form, labDate: e.target.value})} required />
            </div>

            <div className="form-group">
              <label className="form-label">Treatment Cycle</label>
              <input className="form-input" type="text" placeholder="e.g. Sorafenib Cycle 4 Day 23" value={form.treatmentCycle} onChange={e => setForm({...form, treatmentCycle: e.target.value})} />
            </div>

            {[
              { key: 'bilirubin', label: 'Bilirubin (mg/dL)', placeholder: 'e.g. 2.8' },
              { key: 'inr',       label: 'INR',                placeholder: 'e.g. 1.6' },
              { key: 'alt',       label: 'ALT (U/L)',           placeholder: 'e.g. 67' },
              { key: 'ast',       label: 'AST (U/L)',           placeholder: 'e.g. 54' },
              { key: 'albumin',   label: 'Albumin (g/dL)',      placeholder: 'e.g. 3.1 (optional)' },
            ].map(f => (
              <div key={f.key} className="form-group">
                <label className="form-label">{f.label}</label>
                <input className="form-input" type="number" step="0.01" min="0" placeholder={f.placeholder}
                  value={form[f.key]} onChange={e => setForm({...form, [f.key]: e.target.value})}
                  required={f.key !== 'albumin'} />
              </div>
            ))}
          </div>

          <div style={{ background: 'rgba(255,153,0,0.06)', border: '1px solid rgba(255,153,0,0.15)', borderRadius: 10, padding: '12px 16px', marginBottom: 20, fontSize: 12, color: 'var(--muted)' }}>
            ⚡ On submission: drift score will be computed using Isolation Forest + Z-score analysis.
            If risk is MODERATE or HIGH, Amazon Bedrock (Claude 3 Sonnet) will generate a clinical narrative automatically.
          </div>

          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Analysing...' : 'Submit & Analyse'}
          </button>
        </form>
      </div>
    </div>
  );
}
