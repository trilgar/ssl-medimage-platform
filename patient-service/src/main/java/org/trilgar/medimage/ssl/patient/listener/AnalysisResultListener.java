package org.trilgar.medimage.ssl.patient.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.trilgar.medimage.ssl.model.RiskAssessmentResult;
import org.trilgar.medimage.ssl.patient.config.RabbitConfig;
import org.trilgar.medimage.ssl.patient.service.api.PatientService;

@Component
@RequiredArgsConstructor
public class AnalysisResultListener {

    private final PatientService orchestrator;

    @RabbitListener(queues = RabbitConfig.RISK_QUEUE)
    public void onRiskAssessment(RiskAssessmentResult result) {
        orchestrator.processAnalysisResult(result);
    }
}
