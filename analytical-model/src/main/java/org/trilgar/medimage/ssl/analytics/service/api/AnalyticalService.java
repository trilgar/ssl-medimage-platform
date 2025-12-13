package org.trilgar.medimage.ssl.analytics.service.api;

import org.trilgar.medimage.ssl.model.RiskAssessmentResult;

import java.util.UUID;

public interface AnalyticalService {
    RiskAssessmentResult analyze(UUID requestId, UUID patientId, String s3Key, byte[] imageData);
}
