import React, { useState } from 'react';
import Dashboard from './pages/Dashboard';
import { PatientList, LabEntry, AlertsPage } from './pages/Pages';
import './App.css';

export default function App() {
  const [page, setPage]      = useState('dashboard');
  const [patientCode, setPC] = useState('PT-2024-0001');

  return (
    <div className="app">
      <Sidebar page={page} setPage={setPage} />
      <main className="main">
        {page === 'dashboard' && <Dashboard patientCode={patientCode} />}
        {page === 'patients'  && <PatientList onSelect={c => { setPC(c); setPage('dashboard'); }} />}
        {page === 'alerts'    && <AlertsPage />}
        {page === 'lab-entry' && <LabEntry onSuccess={() => setPage('dashboard')} />}
      </main>
    </div>
  );
}

function Sidebar({ page, setPage }) {
  return (
    <aside className="sidebar">
      <div className="logo">
        <div className="logo-mark">
          <div className="logo-icon">🫀</div>
          <div className="logo-name">Liver<span>Care</span></div>
        </div>
        <div className="logo-tag">Drift Detection Engine · v1.0</div>
      </div>
      <nav className="nav">
        <div className="nav-label">Monitor</div>
        <NavItem icon="📊" label="Dashboard"  active={page==='dashboard'} onClick={() => setPage('dashboard')} />
        <NavItem icon="⚠️" label="Alerts"     active={page==='alerts'}   onClick={() => setPage('alerts')} />
        <div className="nav-label" style={{marginTop:16}}>Manage</div>
        <NavItem icon="👤" label="Patients"   active={page==='patients'}  onClick={() => setPage('patients')} />
        <NavItem icon="🔬" label="New Lab"    active={page==='lab-entry'} onClick={() => setPage('lab-entry')} />
      </nav>
      <div className="sidebar-footer">
        <div className="bedrock-badge">⚡ Amazon Bedrock</div>
        <div className="sidebar-sub">Claude 3 Sonnet · AI for Bharat</div>
      </div>
    </aside>
  );
}

function NavItem({ icon, label, active, onClick }) {
  return (
    <div className={`nav-item${active ? ' active' : ''}`} onClick={onClick}>
      <span className="nav-icon">{icon}</span> {label}
    </div>
  );
}
