package com.realintel.livercare.service;

import com.realintel.livercare.dto.*;
import com.realintel.livercare.model.LabResult;
import com.realintel.livercare.model.Patient;
import com.realintel.livercare.repository.LabResultRepository;
import com.realintel.livercare.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabResultService {

    private static final Logger log = LoggerFactory.getLogger(LabResultService.class);

    private final LabResultRepository labRepo;
    private final PatientRepository patientRepo;
    private final BaselineService baselineService;
    private final AiDriftEngineClient aiClient;
    private final DriftAlertService alertService;
    private final PatientService patientService;

    public LabResultService(LabResultRepository labRepo,
                            PatientRepository patientRepo,
                            BaselineService baselineService,
                            AiDriftEngineClient aiClient,
                            DriftAlertService alertService,
                            PatientService patientService) {
        this.labRepo         = labRepo;
        this.patientRepo     = patientRepo;
        this.baselineService = baselineService;
        this.aiClient        = aiClient;
        this.alertService    = alertService;
        this.patientService  = patientService;
    }

    @Transactional
    public LabResultResponse ingest(LabResultRequest req) {
        Patient p = patientRepo.findById(req.getPatientId())
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + req.getPatientId()));

        LabResult lab = new LabResult();
        lab.setPatient(p);
        lab.setLabDate(req.getLabDate());
        lab.setAlt(req.getAlt());
        lab.setAst(req.getAst());
        lab.setBilirubin(req.getBilirubin());
        lab.setInr(req.getInr());
        lab.setAlbumin(req.getAlbumin());
        lab.setAlp(req.getAlp());
        lab.setNotes(req.getNotes());
        lab.setTreatmentCycle(req.getTreatmentCycle());
        lab = labRepo.save(lab);
        log.info("Saved lab {} for patient {}", lab.getId(), p.getPatientCode());

        if (!Boolean.TRUE.equals(p.getBaselineEstablished())) {
            baselineService.establishBaseline(p);
        }

        DriftAnalysisResult drift = null;
        if (Boolean.TRUE.equals(p.getBaselineEstablished())) {
            BaselineStats stats = baselineService.computeStats(p);
            DriftAnalysisRequest dar = new DriftAnalysisRequest();
            dar.setPatientId(p.getId());
            dar.setAlt(lab.getAlt());
            dar.setAst(lab.getAst());
            dar.setBilirubin(lab.getBilirubin());
            dar.setInr(lab.getInr());
            dar.setAlbumin(lab.getAlbumin());
            dar.setTreatmentCycle(lab.getTreatmentCycle());
            dar.setBaselineStats(stats);
            drift = aiClient.analyze(dar);

            p.setDriftScore(drift.getDriftScore());
            p.setCurrentRiskLevel(drift.getRiskLevel());
            patientRepo.save(p);

            if (drift.getRiskLevel() != Patient.RiskLevel.LOW) {
                alertService.createAlert(p, lab, drift, stats);
            }
        }
        return patientService.labToResponse(lab, drift);
    }

    @Transactional(readOnly = true)
    public List<LabResultResponse> getByPatient(Long patientId) {
        Patient p = patientRepo.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientId));
        return labRepo.findByPatientOrderByLabDateDesc(p)
            .stream().map(l -> patientService.labToResponse(l, null)).toList();
    }
}
