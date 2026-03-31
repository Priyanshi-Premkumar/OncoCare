package com.realintel.livercare.repository;

import com.realintel.livercare.model.LabResult;
import com.realintel.livercare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    List<LabResult> findByPatientOrderByLabDateDesc(Patient patient);
    Optional<LabResult> findTopByPatientOrderByLabDateDesc(Patient patient);
    List<LabResult> findByPatientAndLabDateBetweenOrderByLabDateAsc(Patient p, LocalDate from, LocalDate to);
    List<LabResult> findByPatientAndIsBaselineTrueOrderByLabDateAsc(Patient patient);

    @Query(value = "SELECT * FROM lab_results WHERE patient_id = :#{#patient.id} ORDER BY lab_date DESC LIMIT :n", nativeQuery = true)
    List<LabResult> findLatestN(@Param("patient") Patient patient, @Param("n") int n);
}
