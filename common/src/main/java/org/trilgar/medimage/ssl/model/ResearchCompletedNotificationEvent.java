package org.trilgar.medimage.ssl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResearchCompletedNotificationEvent {
    private UUID patientId;
    private UUID requestId;
    private String message;
    private String type;

    private String diagnosis;
    private Double riskScore;
}
