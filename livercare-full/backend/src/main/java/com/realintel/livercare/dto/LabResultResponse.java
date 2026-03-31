package com.realintel.livercare.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LabResultResponse {
    private Long id; private Long patientId; private String patientCode;
    private LocalDate labDate; private Double alt, ast, bilirubin, inr, albumin, alp;
    private String notes; private Boolean isBaseline; private String treatmentCycle;
    private LocalDateTime createdAt; private DriftAnalysisResult driftAnalysis;

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public Long getPatientId() { return patientId; } public void setPatientId(Long v) { patientId = v; }
    public String getPatientCode() { return patientCode; } public void setPatientCode(String v) { patientCode = v; }
    public LocalDate getLabDate() { return labDate; } public void setLabDate(LocalDate v) { labDate = v; }
    public Double getAlt() { return alt; } public void setAlt(Double v) { alt = v; }
    public Double getAst() { return ast; } public void setAst(Double v) { ast = v; }
    public Double getBilirubin() { return bilirubin; } public void setBilirubin(Double v) { bilirubin = v; }
    public Double getInr() { return inr; } public void setInr(Double v) { inr = v; }
    public Double getAlbumin() { return albumin; } public void setAlbumin(Double v) { albumin = v; }
    public Double getAlp() { return alp; } public void setAlp(Double v) { alp = v; }
    public String getNotes() { return notes; } public void setNotes(String v) { notes = v; }
    public Boolean getIsBaseline() { return isBaseline; } public void setIsBaseline(Boolean v) { isBaseline = v; }
    public String getTreatmentCycle() { return treatmentCycle; } public void setTreatmentCycle(String v) { treatmentCycle = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt = v; }
    public DriftAnalysisResult getDriftAnalysis() { return driftAnalysis; } public void setDriftAnalysis(DriftAnalysisResult v) { driftAnalysis = v; }
}
