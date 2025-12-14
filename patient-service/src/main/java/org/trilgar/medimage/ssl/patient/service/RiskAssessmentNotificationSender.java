package org.trilgar.medimage.ssl.patient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.trilgar.medimage.ssl.model.ResearchCompletedNotificationEvent;
import org.trilgar.medimage.ssl.model.RiskAssessmentResult;
import org.trilgar.medimage.ssl.patient.service.api.NotificationSender;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiskAssessmentNotificationSender implements NotificationSender<RiskAssessmentResult> {

    private final RabbitTemplate rabbitTemplate;

    private static final String NOTIFICATION_QUEUE = "notification_queue";

    @Override
    public void sendCompletionNotification(RiskAssessmentResult result) {
        log.info("Sending notification for patient {}", result.getPatientId());

        String type = result.isCritical() ? "CRITICAL" : "INFO";
        String message = String.format("Analysis complete. Diagnosis: %s", result.getDiagnosisLabel());

        ResearchCompletedNotificationEvent event = new ResearchCompletedNotificationEvent(
                result.getPatientId(),
                result.getRequestId(),
                message,
                type,
                result.getDiagnosisLabel(),
                result.getRiskScore()
        );

        rabbitTemplate.convertAndSend(NOTIFICATION_QUEUE, event);
    }
}
