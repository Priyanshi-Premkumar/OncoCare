package com.realintel.livercare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lab_results", indexes = {
    @Index(name = "idx_lab_patient_date", columnList = "patient_id, lab_date DESC"),
    @Index(name = "idx_lab_baseline",     columnList = "patient_id, is_baseline")
})
public class LabResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "lab_date", nullable = false)
    private LocalDate labDate;

    @Column(nullable = false)
    private Double alt;

    @Column(nullable = false)
    private Double ast;

    @Column(nullable = false)
    private Double bilirubin;

    @Column(nullable = false)
    private Double inr;

    private Double albumin;
    private Double alp;

    @Column(length = 300)
    private String notes;

    @Column(name = "is_baseline")
    private Boolean isBaseline = false;

    @Column(name = "treatment_cycle", length = 50)
    private String treatmentCycle;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ── Getters & Setters ─────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long v) { id = v; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient v) { patient = v; }
    public LocalDate getLabDate() { return labDate; }
    public void setLabDate(LocalDate v) { labDate = v; }
    public Double getAlt() { return alt; }
    public void setAlt(Double v) { alt = v; }
    public Double getAst() { return ast; }
    public void setAst(Double v) { ast = v; }
    public Double getBilirubin() { return bilirubin; }
    public void setBilirubin(Double v) { bilirubin = v; }
    public Double getInr() { return inr; }
    public void setInr(Double v) { inr = v; }
    public Double getAlbumin() { return albumin; }
    public void setAlbumin(Double v) { albumin = v; }
    public Double getAlp() { return alp; }
    public void setAlp(Double v) { alp = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { notes = v; }
    public Boolean getIsBaseline() { return isBaseline; }
    public void setIsBaseline(Boolean v) { isBaseline = v; }
    public String getTreatmentCycle() { return treatmentCycle; }
    public void setTreatmentCycle(String v) { treatmentCycle = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { createdAt = v; }
}
