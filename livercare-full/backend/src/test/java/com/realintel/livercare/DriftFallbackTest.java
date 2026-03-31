//package com.realintel.livercare;
//
//import com.realintel.livercare.dto.BaselineStats;
//import com.realintel.livercare.dto.DriftAnalysisRequest;
//import com.realintel.livercare.dto.DriftAnalysisResult;
//import com.realintel.livercare.model.Patient;
//import com.realintel.livercare.service.AiDriftEngineClient;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class DriftFallbackTest {
//
//    @Autowired
//    AiDriftEngineClient client;
//
//    /** Verifies the Z-score fallback produces expected HIGH risk for the demo patient values */
//    @Test
//    void highDriftScoreOnSignificantDeviation() {
//        BaselineStats baseline = BaselineStats.builder()
//            .bilirubinMean(0.82).bilirubinStd(0.02)
//            .inrMean(1.01).inrStd(0.02)
//            .altMean(33.0).altStd(1.5)
//            .astMean(29.0).astStd(1.2)
//            .albuminMean(3.79).albuminStd(0.03)
//            .sampleSize(6)
//            .build();
//
//        DriftAnalysisRequest req = DriftAnalysisRequest.builder()
//            .patientId(1L)
//            .bilirubin(2.80).inr(1.60).alt(67.0).ast(54.0).albumin(3.10)
//            .treatmentCycle("Sorafenib Cycle 4")
//            .baselineStats(baseline)
//            .build();
//
//        DriftAnalysisResult result = client.statisticalFallback(req);
//
//        assertThat(result.getDriftScore()).isGreaterThan(55);
//        assertThat(result.getRiskLevel()).isIn(Patient.RiskLevel.MODERATE, Patient.RiskLevel.HIGH);
//        assertThat(result.getFlaggedMarkers()).contains("bilirubin");
//    }
//
//    @Test
//    void lowDriftForStablePatient() {
//        BaselineStats baseline = BaselineStats.builder()
//            .bilirubinMean(0.71).bilirubinStd(0.02)
//            .inrMean(0.99).inrStd(0.01)
//            .altMean(29.0).altStd(1.0)
//            .astMean(25.0).astStd(1.0)
//            .albuminMean(3.98).albuminStd(0.03)
//            .sampleSize(4)
//            .build();
//
//        DriftAnalysisRequest req = DriftAnalysisRequest.builder()
//            .patientId(2L)
//            .bilirubin(0.74).inr(1.00).alt(30.0).ast(26.0).albumin(3.97)
//            .baselineStats(baseline)
//            .build();
//
//        DriftAnalysisResult result = client.statisticalFallback(req);
//
//        assertThat(result.getDriftScore()).isLessThan(30);
//        assertThat(result.getRiskLevel()).isEqualTo(Patient.RiskLevel.LOW);
//        assertThat(result.getFlaggedMarkers()).isEmpty();
//    }
//}
