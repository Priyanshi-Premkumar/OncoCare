package com.realintel.livercare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients", indexes = {
    @Index(name = "idx_patient_code", columnList = "patient_code", unique = true),
    @Index(name = "idx_patient_risk",  columnList = "current_risk_level")
})
public class Patient {

    public enum Gender     { MALE, FEMALE, OTHER }
    public enum RiskLevel  { LOW, MODERATE, HIGH }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_code", unique = true, nullable = false, length = 20)
    private String patientCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(length = 100)
    private String diagnosis;

    @Column(name = "primary_treatment", length = 100)
    private String primaryTreatment;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_risk_level", length = 20)
    private RiskLevel currentRiskLevel = RiskLevel.LOW;

    @Column(name = "drift_score")
    private Integer driftScore = 0;

    @Column(name = "baseline_established")
    private Boolean baselineEstablished = false;

    @Column(name = "baseline_start_date")
    private LocalDate baselineStartDate;

    @Column(name = "baseline_end_date")
    private LocalDate baselineEndDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ── Getters & Setters ─────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long v) { id = v; }
    public String getPatientCode() { return patientCode; }
    public void setPatientCode(String v) { patientCode = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v) { dateOfBirth = v; }
    public Gender getGender() { return gender; }
    public void setGender(Gender v) { gender = v; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String v) { diagnosis = v; }
    public String getPrimaryTreatment() { return primaryTreatment; }
    public void setPrimaryTreatment(String v) { primaryTreatment = v; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate v) { enrollmentDate = v; }
    public RiskLevel getCurrentRiskLevel() { return currentRiskLevel; }
    public void setCurrentRiskLevel(RiskLevel v) { currentRiskLevel = v; }
    public Integer getDriftScore() { return driftScore; }
    public void setDriftScore(Integer v) { driftScore = v; }
    public Boolean getBaselineEstablished() { return baselineEstablished; }
    public void setBaselineEstablished(Boolean v) { baselineEstablished = v; }
    public LocalDate getBaselineStartDate() { return baselineStartDate; }
    public void setBaselineStartDate(LocalDate v) { baselineStartDate = v; }
    public LocalDate getBaselineEndDate() { return baselineEndDate; }
    public void setBaselineEndDate(LocalDate v) { baselineEndDate = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { createdAt = v; }
}
