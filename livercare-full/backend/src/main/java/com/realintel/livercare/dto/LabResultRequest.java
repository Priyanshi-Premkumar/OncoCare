package com.realintel.livercare.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class LabResultRequest {
    @NotNull private Long patientId;
    @NotNull private LocalDate labDate;
    @NotNull @Positive private Double alt;
    @NotNull @Positive private Double ast;
    @NotNull @Positive private Double bilirubin;
    @NotNull @Positive private Double inr;
    @Positive private Double albumin;
    @Positive private Double alp;
    @Size(max = 300) private String notes;
    private String treatmentCycle;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long v) { this.patientId = v; }
    public LocalDate getLabDate() { return labDate; }
    public void setLabDate(LocalDate v) { this.labDate = v; }
    public Double getAlt() { return alt; }
    public void setAlt(Double v) { this.alt = v; }
    public Double getAst() { return ast; }
    public void setAst(Double v) { this.ast = v; }
    public Double getBilirubin() { return bilirubin; }
    public void setBilirubin(Double v) { this.bilirubin = v; }
    public Double getInr() { return inr; }
    public void setInr(Double v) { this.inr = v; }
    public Double getAlbumin() { return albumin; }
    public void setAlbumin(Double v) { this.albumin = v; }
    public Double getAlp() { return alp; }
    public void setAlp(Double v) { this.alp = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public String getTreatmentCycle() { return treatmentCycle; }
    public void setTreatmentCycle(String v) { this.treatmentCycle = v; }
}
