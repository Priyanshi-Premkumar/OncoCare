package com.realintel.livercare.bedrock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.List;
import java.util.Map;

@Service
public class BedrockNarrativeService {

    private static final Logger log = LoggerFactory.getLogger(BedrockNarrativeService.class);
    private static final String MODEL_ID = "anthropic.claude-3-sonnet-20240229-v1:0";

    private final BedrockRuntimeClient bedrockClient;
    private final ObjectMapper objectMapper;
    private final boolean enabled;

    public BedrockNarrativeService(
            ObjectMapper objectMapper,
            @Value("${aws.region:us-east-1}") String region,
            @Value("${aws.bedrock.enabled:true}") boolean enabled) {
        this.objectMapper = objectMapper;
        this.enabled      = enabled;
        this.bedrockClient = BedrockRuntimeClient.builder()
            .region(Region.of(region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }

    public String generateAlertNarrative(
            String patientCode, String patientName,
            String diagnosis, String treatment,
            int driftScore, String riskLevel,
            List<String> flaggedMarkers, Map<String, Double> weights,
            double bilirubin, double bilirubinBaseline,
            double inr, double inrBaseline,
            double alt, double altBaseline,
            String treatmentCycle) {

        if (!enabled) return fallbackNarrative(patientName, driftScore, riskLevel, bilirubin, bilirubinBaseline);

        String userPrompt = String.format("""
            Patient: %s (%s) | Diagnosis: %s | Treatment: %s | Cycle: %s
            Drift Score: %d/100 | Risk Level: %s
            Current labs vs personal baseline:
            - Bilirubin: %.2f mg/dL (baseline %.2f, delta %+.0f%%)
            - INR: %.2f (baseline %.2f, delta %+.0f%%)
            - ALT: %.0f U/L (baseline %.0f)
            Flagged markers: %s
            Generate a concise 3-4 sentence clinical alert narrative for the oncology team.
            Focus on the clinical significance, likely hepatotoxicity risk, and recommended action.
            """,
            patientCode, patientName, diagnosis, treatment, treatmentCycle,
            driftScore, riskLevel,
            bilirubin, bilirubinBaseline, (bilirubin - bilirubinBaseline) / bilirubinBaseline * 100,
            inr, inrBaseline, (inr - inrBaseline) / Math.max(inrBaseline, 0.01) * 100,
            alt, altBaseline,
            String.join(", ", flaggedMarkers)
        );

        try {
            Map<String, Object> body = Map.of(
                "anthropic_version", "bedrock-2023-05-31",
                "max_tokens", 300,
                "system", "You are an oncology hepatologist assistant. Write precise, actionable clinical narratives for liver toxicity alerts. Use medical terminology appropriate for oncologists.",
                "messages", List.of(Map.of("role", "user", "content", userPrompt))
            );
            String bodyJson = objectMapper.writeValueAsString(body);

            InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(MODEL_ID)
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromUtf8String(bodyJson))
                .build();

            InvokeModelResponse response = bedrockClient.invokeModel(request);
            Map<?, ?> result = objectMapper.readValue(response.body().asUtf8String(), Map.class);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> content = (List<Map<String, Object>>) result.get("content");
            if (content != null && !content.isEmpty()) {
                return (String) content.get(0).get("text");
            }
        } catch (Exception e) {
            log.warn("Bedrock invocation failed: {}. Using fallback narrative.", e.getMessage());
        }
        return fallbackNarrative(patientName, driftScore, riskLevel, bilirubin, bilirubinBaseline);
    }

    private String fallbackNarrative(String name, int score, String risk,
                                     double bilirubin, double baseline) {
        double pct = baseline > 0 ? (bilirubin - baseline) / baseline * 100 : 0;
        return String.format(
            "Patient %s exhibits a %s-risk liver drift pattern with a drift score of %d/100. " +
            "Bilirubin has risen %+.0f%% above personal baseline (current: %.2f mg/dL), " +
            "suggesting emerging hepatotoxicity consistent with Sorafenib-induced liver stress. " +
            "Urgent LFT recheck within 48 hours and hepatology review recommended before next treatment cycle.",
            name, risk.toLowerCase(), score, pct, bilirubin);
    }
}
