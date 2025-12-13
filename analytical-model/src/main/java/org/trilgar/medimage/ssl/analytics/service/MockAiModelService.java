package org.trilgar.medimage.ssl.analytics.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.trilgar.medimage.ssl.analytics.service.api.AnalyticalService;
import org.trilgar.medimage.ssl.model.RiskAssessmentResult;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class MockAiModelService implements AnalyticalService {
    private final Random random = new Random();

    public RiskAssessmentResult analyze(UUID requestId, UUID patientId, String s3Key, byte[] imageData) {
        log.info("Starting AI inference for Request: {} (Image size: {} bytes)", requestId, imageData.length);

        // 1. Emulate heavy calculations
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 2. Generate random result
        double riskScore = random.nextDouble();
        boolean isCritical = riskScore > 0.70;

        String label;
        if (riskScore > 0.85) label = "HIGH_RISK_PNEUMONIA";
        else if (riskScore > 0.50) label = "MODERATE_RISK";
        else label = "NO_PATHOLOGY";

        log.info("Inference complete. Score: {}", String.format("%.2f", riskScore));

        // 3. Return DTO
        return new RiskAssessmentResult(
                requestId,
                patientId,
                s3Key,
                riskScore,
                label,
                isCritical,
                LocalDateTime.now()
        );
    }
}
