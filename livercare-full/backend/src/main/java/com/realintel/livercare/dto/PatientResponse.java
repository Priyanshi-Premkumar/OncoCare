package com.realintel.livercare.dto;

import com.realintel.livercare.model.Patient;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PatientResponse {
    private Long id; private String patientCode; private String fullName;
    private LocalDate dateOfBirth; private Patient.Gender gender;
    private String diagnosis; private String primaryTreatment;
    private LocalDate enrollmentDate; private Patient.RiskLevel currentRiskLevel;
    private Integer driftScore; private Boolean baselineEstablished;
    private LocalDate baselineStartDate; private LocalDate baselineEndDate;
    private LocalDateTime createdAt;

    public Long getId() { return id; } public void setId(Long v) { id = v; }
    public String getPatientCode() { return patientCode; } public void setPatientCode(String v) { patientCode = v; }
    public String getFullName() { return fullName; } public void setFullName(String v) { fullName = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; } public void setDateOfBirth(LocalDate v) { dateOfBirth = v; }
    public Patient.Gender getGender() { return gender; } public void setGender(Patient.Gender v) { gender = v; }
    public String getDiagnosis() { return diagnosis; } public void setDiagnosis(String v) { diagnosis = v; }
    public String getPrimaryTreatment() { return primaryTreatment; } public void setPrimaryTreatment(String v) { primaryTreatment = v; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; } public void setEnrollmentDate(LocalDate v) { enrollmentDate = v; }
    public Patient.RiskLevel getCurrentRiskLevel() { return currentRiskLevel; } public void setCurrentRiskLevel(Patient.RiskLevel v) { currentRiskLevel = v; }
    public Integer getDriftScore() { return driftScore; } public void setDriftScore(Integer v) { driftScore = v; }
    public Boolean getBaselineEstablished() { return baselineEstablished; } public void setBaselineEstablished(Boolean v) { baselineEstablished = v; }
    public LocalDate getBaselineStartDate() { return baselineStartDate; } public void setBaselineStartDate(LocalDate v) { baselineStartDate = v; }
    public LocalDate getBaselineEndDate() { return baselineEndDate; } public void setBaselineEndDate(LocalDate v) { baselineEndDate = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { createdAt = v; }
}
