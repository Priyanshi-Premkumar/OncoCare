import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || '/api',
  auth: {
    username: process.env.REACT_APP_API_USER || 'admin',
    password: process.env.REACT_APP_API_PASS || 'livercare2024',
  },
  headers: { 'Content-Type': 'application/json' },
});

// ── Patients ──────────────────────────────────────────
export const registerPatient   = (data)  => api.post('/patients', data);
export const listPatients      = ()      => api.get('/patients');
export const getAtRiskPatients = ()      => api.get('/patients/at-risk');
export const getPatientByCode  = (code)  => api.get(`/patients/${code}`);
export const getPatientDashboard = (code) => api.get(`/patients/${code}/dashboard`);

// ── Labs ──────────────────────────────────────────────
export const submitLab         = (data)      => api.post('/labs', data);
export const getPatientLabs    = (patientId) => api.get(`/labs/patient/${patientId}`);

// ── Alerts ────────────────────────────────────────────
export const getAlerts         = ()           => api.get('/alerts');
export const resolveAlert      = (id, data)   => api.patch(`/alerts/${id}/resolve`, data);

export default api;
