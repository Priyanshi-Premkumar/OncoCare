package com.realintel.livercare.dto;

public class DriftAnalysisRequest {
    private Long patientId;
    private Double alt, ast, bilirubin, inr, albumin;
    private String treatmentCycle;
    private BaselineStats baselineStats;

    public Long getPatientId() { return patientId; } public void setPatientId(Long v) { patientId = v; }
    public Double getAlt() { return alt; } public void setAlt(Double v) { alt = v; }
    public Double getAst() { return ast; } public void setAst(Double v) { ast = v; }
    public Double getBilirubin() { return bilirubin; } public void setBilirubin(Double v) { bilirubin = v; }
    public Double getInr() { return inr; } public void setInr(Double v) { inr = v; }
    public Double getAlbumin() { return albumin; } public void setAlbumin(Double v) { albumin = v; }
    public String getTreatmentCycle() { return treatmentCycle; } public void setTreatmentCycle(String v) { treatmentCycle = v; }
    public BaselineStats getBaselineStats() { return baselineStats; } public void setBaselineStats(BaselineStats v) { baselineStats = v; }
}
