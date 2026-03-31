package com.realintel.livercare.repository;

import com.realintel.livercare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientCode(String patientCode);
    boolean existsByPatientCode(String patientCode);

    @Query("SELECT p FROM Patient p WHERE p.currentRiskLevel IN ('MODERATE','HIGH') ORDER BY p.driftScore DESC")
    List<Patient> findAllAtRisk();

    @Query("SELECT p FROM Patient p WHERE p.baselineEstablished = false AND p.enrollmentDate <= :cutoff")
    List<Patient> findPatientsNeedingBaseline(@Param("cutoff") LocalDate cutoff);
}
