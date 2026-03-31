package com.realintel.livercare.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "drift_alerts", indexes = {
    @Index(name = "idx_alert_patient",    columnList = "patient_id"),
    @Index(name = "idx_alert_unresolved", columnList = "patient_id, resolved"),
    @Index(name = "idx_alert_risk",       columnList = "risk_level, resolved")
})
public class DriftAlert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_result_id")
    private LabResult triggeringLabResult;

    @Column(nullable = false)
    private Integer driftScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Patient.RiskLevel riskLevel;

    @Column(name = "marker_weights_json", columnDefinition = "TEXT")
    private String markerWeightsJson;

    @Column(name = "flagged_markers", length = 200)
    private String flaggedMarkers;

    @Column(name = "ai_explanation", columnDefinition = "TEXT")
    private String aiExplanation;

    @Column(name = "bedrock_narrative", columnDefinition = "TEXT")
    private String bedrockNarrative;

    @Column(name = "days_ahead_threshold")
    private Integer daysAheadThreshold;

    @Column(nullable = false)
    private Boolean resolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by", length = 100)
    private String resolvedBy;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ── Getters & Setters ─────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long v) { id = v; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient v) { patient = v; }
    public LabResult getTriggeringLabResult() { return triggeringLabResult; }
    public void setTriggeringLabResult(LabResult v) { triggeringLabResult = v; }
    public Integer getDriftScore() { return driftScore; }
    public void setDriftScore(Integer v) { driftScore = v; }
    public Patient.RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(Patient.RiskLevel v) { riskLevel = v; }
    public String getMarkerWeightsJson() { return markerWeightsJson; }
    public void setMarkerWeightsJson(String v) { markerWeightsJson = v; }
    public String getFlaggedMarkers() { return flaggedMarkers; }
    public void setFlaggedMarkers(String v) { flaggedMarkers = v; }
    public String getAiExplanation() { return aiExplanation; }
    public void setAiExplanation(String v) { aiExplanation = v; }
    public String getBedrockNarrative() { return bedrockNarrative; }
    public void setBedrockNarrative(String v) { bedrockNarrative = v; }
    public Integer getDaysAheadThreshold() { return daysAheadThreshold; }
    public void setDaysAheadThreshold(Integer v) { daysAheadThreshold = v; }
    public Boolean getResolved() { return resolved; }
    public void setResolved(Boolean v) { resolved = v; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime v) { resolvedAt = v; }
    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String v) { resolvedBy = v; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String v) { resolutionNotes = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { createdAt = v; }
}
