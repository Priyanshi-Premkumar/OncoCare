package com.realintel.livercare.dto;

import java.util.List;

public class PatientDashboard {
    private PatientResponse patient;
    private LabResultResponse latestLab;
    private DriftAnalysisResult latestDrift;
    private List<AlertResponse> unresolvedAlerts;
    private List<LabResultResponse> recentLabs;
    private BaselineStats baselineStats;
    private List<AlertResponse> alertHistory;

    public PatientResponse getPatient() { return patient; } public void setPatient(PatientResponse v) { patient = v; }
    public LabResultResponse getLatestLab() { return latestLab; } public void setLatestLab(LabResultResponse v) { latestLab = v; }
    public DriftAnalysisResult getLatestDrift() { return latestDrift; } public void setLatestDrift(DriftAnalysisResult v) { latestDrift = v; }
    public List<AlertResponse> getUnresolvedAlerts() { return unresolvedAlerts; } public void setUnresolvedAlerts(List<AlertResponse> v) { unresolvedAlerts = v; }
    public List<LabResultResponse> getRecentLabs() { return recentLabs; } public void setRecentLabs(List<LabResultResponse> v) { recentLabs = v; }
    public BaselineStats getBaselineStats() { return baselineStats; } public void setBaselineStats(BaselineStats v) { baselineStats = v; }
    public List<AlertResponse> getAlertHistory() { return alertHistory; } public void setAlertHistory(List<AlertResponse> v) { alertHistory = v; }
}
