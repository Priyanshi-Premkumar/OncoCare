package com.realintel.livercare.dto;

import com.realintel.livercare.model.Patient;
import java.util.List;
import java.util.Map;

public class DriftAnalysisResult {
    private Integer driftScore;
    private Patient.RiskLevel riskLevel;
    private Map<String, Double> markerWeights;
    private List<String> flaggedMarkers;
    private String explanation;
    private Integer daysAheadOfThreshold;

    public Integer getDriftScore() { return driftScore; } public void setDriftScore(Integer v) { driftScore = v; }
    public Patient.RiskLevel getRiskLevel() { return riskLevel; } public void setRiskLevel(Patient.RiskLevel v) { riskLevel = v; }
    public Map<String, Double> getMarkerWeights() { return markerWeights; } public void setMarkerWeights(Map<String, Double> v) { markerWeights = v; }
    public List<String> getFlaggedMarkers() { return flaggedMarkers; } public void setFlaggedMarkers(List<String> v) { flaggedMarkers = v; }
    public String getExplanation() { return explanation; } public void setExplanation(String v) { explanation = v; }
    public Integer getDaysAheadOfThreshold() { return daysAheadOfThreshold; } public void setDaysAheadOfThreshold(Integer v) { daysAheadOfThreshold = v; }
}
