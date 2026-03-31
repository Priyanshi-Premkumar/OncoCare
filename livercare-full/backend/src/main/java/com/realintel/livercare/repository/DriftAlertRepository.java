package com.realintel.livercare.repository;

import com.realintel.livercare.model.DriftAlert;
import com.realintel.livercare.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriftAlertRepository extends JpaRepository<DriftAlert, Long> {
    List<DriftAlert> findByPatientAndResolvedFalseOrderByCreatedAtDesc(Patient patient);
    List<DriftAlert> findByResolvedFalseOrderByDriftScoreDesc();
    List<DriftAlert> findByPatientOrderByCreatedAtDesc(Patient patient);

    @Query("SELECT da FROM DriftAlert da WHERE da.riskLevel = 'HIGH' AND da.resolved = false ORDER BY da.driftScore DESC")
    List<DriftAlert> findAllUnresolvedHighRisk();
}
