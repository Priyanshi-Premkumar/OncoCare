package com.realintel.livercare.dto;

import com.realintel.livercare.model.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class PatientRequest {
    @NotBlank @Size(max = 100) private String fullName;
    @NotNull private LocalDate dateOfBirth;
    @NotNull private Patient.Gender gender;
    @Size(max = 100) private String diagnosis;
    @Size(max = 100) private String primaryTreatment;
    @NotNull private LocalDate enrollmentDate;

    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v) { this.dateOfBirth = v; }
    public Patient.Gender getGender() { return gender; }
    public void setGender(Patient.Gender v) { this.gender = v; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String v) { this.diagnosis = v; }
    public String getPrimaryTreatment() { return primaryTreatment; }
    public void setPrimaryTreatment(String v) { this.primaryTreatment = v; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate v) { this.enrollmentDate = v; }
}
