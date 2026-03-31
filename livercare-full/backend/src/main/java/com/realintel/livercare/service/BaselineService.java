package com.realintel.livercare.service;

import com.realintel.livercare.dto.BaselineStats;
import com.realintel.livercare.model.LabResult;
import com.realintel.livercare.model.Patient;
import com.realintel.livercare.repository.LabResultRepository;
import com.realintel.livercare.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BaselineService {

    private static final Logger log = LoggerFactory.getLogger(BaselineService.class);
    private static final int MIN_SAMPLES = 4;
    private static final int WINDOW_DAYS = 60;

    private final PatientRepository patientRepo;
    private final LabResultRepository labRepo;

    public BaselineService(PatientRepository patientRepo, LabResultRepository labRepo) {
        this.patientRepo = patientRepo;
        this.labRepo     = labRepo;
    }

    @Transactional
    public boolean establishBaseline(Patient patient) {
        LocalDate start = patient.getEnrollmentDate();
        LocalDate end   = start.plusDays(WINDOW_DAYS);
        List<LabResult> window = labRepo.findByPatientAndLabDateBetweenOrderByLabDateAsc(patient, start, end);
        if (window.size() < MIN_SAMPLES) {
            log.info("Patient {} has {} results - need {} for baseline",
                patient.getPatientCode(), window.size(), MIN_SAMPLES);
            return false;
        }
        window.forEach(r -> r.setIsBaseline(true));
        labRepo.saveAll(window);
        patient.setBaselineEstablished(true);
        patient.setBaselineStartDate(window.get(0).getLabDate());
        patient.setBaselineEndDate(window.get(window.size() - 1).getLabDate());
        patientRepo.save(patient);
        log.info("Baseline established for {} using {} samples", patient.getPatientCode(), window.size());
        return true;
    }

    @Transactional(readOnly = true)
    public BaselineStats computeStats(Patient patient) {
        List<LabResult> base = labRepo.findByPatientAndIsBaselineTrueOrderByLabDateAsc(patient);
        if (base.isEmpty()) return null;

        BaselineStats s = new BaselineStats();
        s.setAltMean(mean(base, "alt"));         s.setAltStd(std(base, "alt"));
        s.setAstMean(mean(base, "ast"));         s.setAstStd(std(base, "ast"));
        s.setBilirubinMean(mean(base,"bilirubin")); s.setBilirubinStd(std(base,"bilirubin"));
        s.setInrMean(mean(base, "inr"));         s.setInrStd(std(base, "inr"));
        s.setAlbuminMean(mean(base, "albumin")); s.setAlbuminStd(std(base, "albumin"));
        s.setSampleSize(base.size());
        return s;
    }

    private double mean(List<LabResult> results, String m) {
        return results.stream().mapToDouble(r -> val(r, m)).filter(v -> v > 0).average().orElse(0.0);
    }

    private double std(List<LabResult> results, String m) {
        double mu  = mean(results, m);
        double var = results.stream().mapToDouble(r -> val(r, m)).filter(v -> v > 0)
            .map(v -> Math.pow(v - mu, 2)).average().orElse(0.0);
        return Math.sqrt(var);
    }

    private double val(LabResult r, String m) {
        return switch (m) {
            case "alt"       -> r.getAlt()       != null ? r.getAlt()       : 0.0;
            case "ast"       -> r.getAst()       != null ? r.getAst()       : 0.0;
            case "bilirubin" -> r.getBilirubin() != null ? r.getBilirubin() : 0.0;
            case "inr"       -> r.getInr()       != null ? r.getInr()       : 0.0;
            case "albumin"   -> r.getAlbumin()   != null ? r.getAlbumin()   : 0.0;
            default          -> 0.0;
        };
    }
}
