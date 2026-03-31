package com.realintel.livercare.service;

import com.realintel.livercare.dto.*;
import com.realintel.livercare.model.DriftAlert;
import com.realintel.livercare.model.LabResult;
import com.realintel.livercare.model.Patient;
import com.realintel.livercare.repository.LabResultRepository;
import com.realintel.livercare.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private final PatientRepository patientRepo;
    private final LabResultRepository labRepo;
    private final BaselineService baselineService;
    private final DriftAlertService alertService;

    public PatientService(PatientRepository patientRepo,
                          LabResultRepository labRepo,
                          BaselineService baselineService,
                          DriftAlertService alertService) {
        this.patientRepo     = patientRepo;
        this.labRepo         = labRepo;
        this.baselineService = baselineService;
        this.alertService    = alertService;
    }

    @Transactional
    public PatientResponse register(PatientRequest req) {
        String code = generateCode();
        Patient p = new Patient();
        p.setPatientCode(code);
        p.setFullName(req.getFullName());
        p.setDateOfBirth(req.getDateOfBirth());
        p.setGender(req.getGender());
        p.setDiagnosis(req.getDiagnosis());
        p.setPrimaryTreatment(req.getPrimaryTreatment());
        p.setEnrollmentDate(req.getEnrollmentDate());
        patientRepo.save(p);
        log.info("Registered patient: {} ({})", code, req.getFullName());
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public PatientResponse getByCode(String code) {
        return toResponse(findOrThrow(code));
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAll() {
        return patientRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAtRisk() {
        return patientRepo.findAllAtRisk().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PatientDashboard getDashboard(String code) {
        Patient p = findOrThrow(code);

        LabResultResponse latestLab = labRepo.findTopByPatientOrderByLabDateDesc(p)
            .map(l -> labToResponse(l, null)).orElse(null);

        List<LabResultResponse> recent = labRepo.findLatestN(p, 10)
            .stream().map(l -> labToResponse(l, null)).toList();

        List<AlertResponse> alerts = alertService.getPatientAlerts(p);
        List<AlertResponse> unresolved = alerts.stream().filter(a -> !a.getResolved()).toList();

        BaselineStats stats = Boolean.TRUE.equals(p.getBaselineEstablished())
            ? baselineService.computeStats(p) : null;

        PatientDashboard dashboard = new PatientDashboard();
        dashboard.setPatient(toResponse(p));
        dashboard.setLatestLab(latestLab);
        dashboard.setRecentLabs(recent);
        dashboard.setUnresolvedAlerts(unresolved);
        dashboard.setAlertHistory(alerts);
        dashboard.setBaselineStats(stats);
        return dashboard;
    }

    public Patient findOrThrow(String code) {
        return patientRepo.findByPatientCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + code));
    }

    private String generateCode() {
        String yr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String code;
        do { code = "PT-" + yr + "-" + String.format("%04d", COUNTER.getAndIncrement()); }
        while (patientRepo.existsByPatientCode(code));
        return code;
    }

    public PatientResponse toResponse(Patient p) {
        PatientResponse r = new PatientResponse();
        r.setId(p.getId());
        r.setPatientCode(p.getPatientCode());
        r.setFullName(p.getFullName());
        r.setDateOfBirth(p.getDateOfBirth());
        r.setGender(p.getGender());
        r.setDiagnosis(p.getDiagnosis());
        r.setPrimaryTreatment(p.getPrimaryTreatment());
        r.setEnrollmentDate(p.getEnrollmentDate());
        r.setCurrentRiskLevel(p.getCurrentRiskLevel());
        r.setDriftScore(p.getDriftScore());
        r.setBaselineEstablished(p.getBaselineEstablished());
        r.setBaselineStartDate(p.getBaselineStartDate());
        r.setBaselineEndDate(p.getBaselineEndDate());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }

    public LabResultResponse labToResponse(LabResult l, DriftAnalysisResult drift) {
        LabResultResponse r = new LabResultResponse();
        r.setId(l.getId());
        r.setPatientId(l.getPatient().getId());
        r.setPatientCode(l.getPatient().getPatientCode());
        r.setLabDate(l.getLabDate());
        r.setAlt(l.getAlt());
        r.setAst(l.getAst());
        r.setBilirubin(l.getBilirubin());
        r.setInr(l.getInr());
        r.setAlbumin(l.getAlbumin());
        r.setAlp(l.getAlp());
        r.setNotes(l.getNotes());
        r.setIsBaseline(l.getIsBaseline());
        r.setTreatmentCycle(l.getTreatmentCycle());
        r.setCreatedAt(l.getCreatedAt());
        r.setDriftAnalysis(drift);
        return r;
    }
}
