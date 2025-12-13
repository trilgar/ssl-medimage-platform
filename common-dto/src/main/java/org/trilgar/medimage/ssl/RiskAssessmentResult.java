package org.trilgar.medimage.ssl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentResult {
    private UUID requestId;
    private UUID patientId;

    private double riskScore;
    private String diagnosisLabel;
    private boolean isCritical;

    private LocalDateTime analyzedAt;
}