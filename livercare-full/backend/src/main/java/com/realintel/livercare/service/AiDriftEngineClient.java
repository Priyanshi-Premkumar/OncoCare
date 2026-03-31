package com.realintel.livercare.service;

import com.realintel.livercare.dto.BaselineStats;
import com.realintel.livercare.dto.DriftAnalysisRequest;
import com.realintel.livercare.dto.DriftAnalysisResult;
import com.realintel.livercare.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiDriftEngineClient {

    private static final Logger log = LoggerFactory.getLogger(AiDriftEngineClient.class);

    private final WebClient webClient;

    @Value("${ai.drift-engine.timeout-seconds:10}")
    private int timeoutSeconds;

    public AiDriftEngineClient(WebClient.Builder webClientBuilder,
                               @Value("${ai.drift-engine.base-url:http://localhost:8000}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public DriftAnalysisResult analyze(DriftAnalysisRequest req) {
        try {
            // Build Python-engine payload
            Map<String, Object> payload = buildPayload(req);
            Map<?, ?> response = webClient.post()
                .uri("/analyze")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .block();

            return parseResponse(response);
        } catch (Exception ex) {
            log.warn("AI engine unreachable ({}), using statistical fallback", ex.getMessage());
            return statisticalFallback(req);
        }
    }

    private Map<String, Object> buildPayload(DriftAnalysisRequest req) {
        BaselineStats bs = req.getBaselineStats();
        Map<String, Object> bsMap = new HashMap<>();
        if (bs != null) {
            bsMap.put("alt_mean",       bs.getAltMean());
            bsMap.put("alt_std",        bs.getAltStd());
            bsMap.put("ast_mean",       bs.getAstMean());
            bsMap.put("ast_std",        bs.getAstStd());
            bsMap.put("bilirubin_mean", bs.getBilirubinMean());
            bsMap.put("bilirubin_std",  bs.getBilirubinStd());
            bsMap.put("inr_mean",       bs.getInrMean());
            bsMap.put("inr_std",        bs.getInrStd());
            bsMap.put("albumin_mean",   bs.getAlbuminMean());
            bsMap.put("albumin_std",    bs.getAlbuminStd());
            bsMap.put("sample_size",    bs.getSampleSize());
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("patient_id",     req.getPatientId());
        payload.put("alt",            req.getAlt());
        payload.put("ast",            req.getAst());
        payload.put("bilirubin",      req.getBilirubin());
        payload.put("inr",            req.getInr());
        payload.put("albumin",        req.getAlbumin());
        payload.put("treatment_cycle",req.getTreatmentCycle());
        payload.put("baseline_stats", bsMap);
        return payload;
    }

    @SuppressWarnings("unchecked")
    private DriftAnalysisResult parseResponse(Map<?, ?> r) {
        DriftAnalysisResult result = new DriftAnalysisResult();
        result.setDriftScore(((Number) r.get("drift_score")).intValue());
        result.setRiskLevel(Patient.RiskLevel.valueOf((String) r.get("risk_level")));
        result.setFlaggedMarkers((List<String>) r.get("flagged_markers"));
        result.setExplanation((String) r.get("explanation"));
        Object daysObj = r.get("days_ahead_of_threshold");
        if (daysObj instanceof Number) result.setDaysAheadOfThreshold(((Number) daysObj).intValue());
        Map<String, Double> weights = new HashMap<>();
        Object wObj = r.get("marker_weights");
        if (wObj instanceof Map<?,?> wMap) {
            wMap.forEach((k, v) -> weights.put(k.toString(), ((Number) v).doubleValue()));
        }
        result.setMarkerWeights(weights);
        return result;
    }

    /** Z-score + sigmoid fallback when Python engine is unreachable */
    public DriftAnalysisResult statisticalFallback(DriftAnalysisRequest req) {
        BaselineStats bs = req.getBaselineStats();
        if (bs == null) {
            DriftAnalysisResult r = new DriftAnalysisResult();
            r.setDriftScore(0); r.setRiskLevel(Patient.RiskLevel.LOW);
            r.setExplanation("No baseline available for comparison.");
            return r;
        }

        Map<String, Double> weights = Map.of(
            "bilirubin", 0.35, "inr", 0.25, "alt", 0.20, "ast", 0.12, "albumin", 0.08);

        double bilZ = zScore(req.getBilirubin(), bs.getBilirubinMean(), bs.getBilirubinStd());
        double inrZ = zScore(req.getInr(),       bs.getInrMean(),       bs.getInrStd());
        double altZ = zScore(req.getAlt(),       bs.getAltMean(),       bs.getAltStd());
        double astZ = zScore(req.getAst(),       bs.getAstMean(),       bs.getAstStd());
        double albZ = req.getAlbumin() != null
            ? zScore(req.getAlbumin(), bs.getAlbuminMean(), bs.getAlbuminStd()) : 0.0;

        double combined =
            weights.get("bilirubin") * sigmoid(bilZ) +
            weights.get("inr")       * sigmoid(inrZ) +
            weights.get("alt")       * sigmoid(altZ) +
            weights.get("ast")       * sigmoid(astZ) +
            weights.get("albumin")   * sigmoid(-albZ);

        int score = (int) Math.min(100, Math.max(0, combined * 100));
        Patient.RiskLevel risk = score >= 60 ? Patient.RiskLevel.HIGH
            : score >= 35 ? Patient.RiskLevel.MODERATE : Patient.RiskLevel.LOW;

        List<String> flagged = new java.util.ArrayList<>();
        if (bilZ > 1.5) flagged.add("bilirubin");
        if (inrZ > 1.5) flagged.add("inr");
        if (altZ > 1.5) flagged.add("alt");
        if (astZ > 1.5) flagged.add("ast");
        if (albZ < -1.5) flagged.add("albumin");

        double bilPct = bs.getBilirubinMean() > 0
            ? (req.getBilirubin() - bs.getBilirubinMean()) / bs.getBilirubinMean() * 100 : 0;

        Integer daysAhead = score >= 70 ? 7 : score >= 55 ? 10 : score >= 35 ? 14 : null;

        DriftAnalysisResult result = new DriftAnalysisResult();
        result.setDriftScore(score);
        result.setRiskLevel(risk);
        result.setMarkerWeights(weights);
        result.setFlaggedMarkers(flagged);
        result.setDaysAheadOfThreshold(daysAhead);
        result.setExplanation(String.format(
            "Statistical fallback — Drift score %d/100 (%s). Bilirubin %+.0f%% from baseline. Flagged: %s.",
            score, risk, bilPct, flagged.isEmpty() ? "none" : String.join(", ", flagged)));
        return result;
    }

    private double zScore(double val, double mean, double std) {
        return std > 0 ? (val - mean) / std : 0.0;
    }

    private double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }
}
