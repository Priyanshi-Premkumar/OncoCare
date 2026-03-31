package com.realintel.livercare.dto;

import com.realintel.livercare.model.Patient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AlertResponse {
    private Long id; private Long patientId; private String patientCode; private String patientName;
    private Integer driftScore; private Patient.RiskLevel riskLevel;
    private Map<String, Double> markerWeights; private List<String> flaggedMarkers;
    private String aiExplanation; private String bedrockNarrative;
    private Integer daysAheadThreshold; private Boolean resolved; private LocalDateTime createdAt;

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public Long getPatientId() { return patientId; } public void setPatientId(Long v) { patientId = v; }
    public String getPatientCode() { return patientCode; } public void setPatientCode(String v) { patientCode = v; }
    public String getPatientName() { return patientName; } public void setPatientName(String v) { patientName = v; }
    public Integer getDriftScore() { return driftScore; } public void setDriftScore(Integer v) { driftScore = v; }
    public Patient.RiskLevel getRiskLevel() { return riskLevel; } public void setRiskLevel(Patient.RiskLevel v) { riskLevel = v; }
    public Map<String, Double> getMarkerWeights() { return markerWeights; } public void setMarkerWeights(Map<String, Double> v) { markerWeights = v; }
    public List<String> getFlaggedMarkers() { return flaggedMarkers; } public void setFlaggedMarkers(List<String> v) { flaggedMarkers = v; }
    public String getAiExplanation() { return aiExplanation; } public void setAiExplanation(String v) { aiExplanation = v; }
    public String getBedrockNarrative() { return bedrockNarrative; } public void setBedrockNarrative(String v) { bedrockNarrative = v; }
    public Integer getDaysAheadThreshold() { return daysAheadThreshold; } public void setDaysAheadThreshold(Integer v) { daysAheadThreshold = v; }
    public Boolean getResolved() { return resolved; } public void setResolved(Boolean v) { resolved = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt = v; }
}
