package com.realintel.livercare.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realintel.livercare.bedrock.BedrockNarrativeService;
import com.realintel.livercare.dto.*;
import com.realintel.livercare.model.DriftAlert;
import com.realintel.livercare.model.LabResult;
import com.realintel.livercare.model.Patient;
import com.realintel.livercare.repository.DriftAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DriftAlertService {

    private static final Logger log = LoggerFactory.getLogger(DriftAlertService.class);

    private final DriftAlertRepository alertRepo;
    private final BedrockNarrativeService bedrock;
    private final ObjectMapper objectMapper;

    public DriftAlertService(DriftAlertRepository alertRepo,
                             BedrockNarrativeService bedrock,
                             ObjectMapper objectMapper) {
        this.alertRepo    = alertRepo;
        this.bedrock      = bedrock;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public DriftAlert createAlert(Patient p, LabResult lab, DriftAnalysisResult drift, BaselineStats bs) {
        List<String> flaggedList = drift.getFlaggedMarkers() != null
            ? drift.getFlaggedMarkers() : List.of();

        String bedrockNarrative = bedrock.generateAlertNarrative(
            p.getPatientCode(), p.getFullName(),
            p.getDiagnosis(), p.getPrimaryTreatment(),
            drift.getDriftScore(), drift.getRiskLevel().name(),
            flaggedList, drift.getMarkerWeights(),
            lab.getBilirubin(), bs != null ? bs.getBilirubinMean() : 0.8,
            lab.getInr(),       bs != null ? bs.getInrMean()       : 1.0,
            lab.getAlt(),       bs != null ? bs.getAltMean()       : 30.0,
            lab.getTreatmentCycle()
        );

        String weightsJson = null;
        String flaggedStr  = null;
        try {
            if (drift.getMarkerWeights() != null)
                weightsJson = objectMapper.writeValueAsString(drift.getMarkerWeights());
            if (!flaggedList.isEmpty())
                flaggedStr = String.join(",", flaggedList);
        } catch (Exception ignored) {}

        DriftAlert alert = new DriftAlert();
        alert.setPatient(p);
        alert.setTriggeringLabResult(lab);
        alert.setDriftScore(drift.getDriftScore());
        alert.setRiskLevel(drift.getRiskLevel());
        alert.setMarkerWeightsJson(weightsJson);
        alert.setFlaggedMarkers(flaggedStr);
        alert.setAiExplanation(drift.getExplanation());
        alert.setBedrockNarrative(bedrockNarrative);
        alert.setDaysAheadThreshold(drift.getDaysAheadOfThreshold());
        alert = alertRepo.save(alert);

        log.warn("DRIFT ALERT id={} patient={} score={} risk={}",
            alert.getId(), p.getPatientCode(), drift.getDriftScore(), drift.getRiskLevel());
        return alert;
    }

    @Transactional
    public DriftAlert resolve(Long alertId, AlertResolveRequest req) {
        DriftAlert a = alertRepo.findById(alertId)
            .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        a.setResolved(true);
        a.setResolvedAt(LocalDateTime.now());
        a.setResolvedBy(req.getResolvedBy());
        a.setResolutionNotes(req.getResolutionNotes());
        return alertRepo.save(a);
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getUnresolved() {
        return alertRepo.findByResolvedFalseOrderByDriftScoreDesc()
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getPatientAlerts(Patient p) {
        return alertRepo.findByPatientOrderByCreatedAtDesc(p)
            .stream().map(this::toResponse).toList();
    }

    @SuppressWarnings("unchecked")
    private AlertResponse toResponse(DriftAlert a) {
        Map<String, Double> weights = null;
        List<String> flagged = null;
        try {
            if (a.getMarkerWeightsJson() != null)
                weights = objectMapper.readValue(a.getMarkerWeightsJson(),
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Double.class));
            if (a.getFlaggedMarkers() != null && !a.getFlaggedMarkers().isBlank())
                flagged = List.of(a.getFlaggedMarkers().split(","));
        } catch (Exception ignored) {}

        AlertResponse r = new AlertResponse();
        r.setId(a.getId());
        r.setPatientId(a.getPatient().getId());
        r.setPatientCode(a.getPatient().getPatientCode());
        r.setPatientName(a.getPatient().getFullName());
        r.setDriftScore(a.getDriftScore());
        r.setRiskLevel(a.getRiskLevel());
        r.setMarkerWeights(weights);
        r.setFlaggedMarkers(flagged);
        r.setAiExplanation(a.getAiExplanation());
        r.setBedrockNarrative(a.getBedrockNarrative());
        r.setDaysAheadThreshold(a.getDaysAheadThreshold());
        r.setResolved(a.getResolved());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}
