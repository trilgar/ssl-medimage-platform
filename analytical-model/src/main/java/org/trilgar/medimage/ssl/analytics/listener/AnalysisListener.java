package org.trilgar.medimage.ssl.analytics.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.trilgar.medimage.ssl.analytics.config.RabbitConfig;
import org.trilgar.medimage.ssl.analytics.service.MockAiModelService;
import org.trilgar.medimage.ssl.analytics.service.api.AnalyticalService;
import org.trilgar.medimage.ssl.model.ImageAnalysisRequest;
import org.trilgar.medimage.ssl.model.RiskAssessmentResult;
import org.trilgar.medimage.ssl.s3.api.S3StorageService;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisListener {
    private final S3StorageService s3Service;
    private final AnalyticalService aiService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.ANALYSIS_INPUT_QUEUE)
    public void processAnalysisRequest(ImageAnalysisRequest request) {
        log.info("Received task for Patient: {}", request.getPatientId());

        try {
            byte[] imageData = s3Service.downloadImage(request.getS3ObjectKey());

            RiskAssessmentResult result = aiService.analyze(
                    request.getRequestId(),
                    request.getPatientId(),
                    request.getS3ObjectKey(),
                    imageData
            );

            rabbitTemplate.convertAndSend(RabbitConfig.RISK_OUTPUT_QUEUE, result);

            log.info("Result sent to queue: {}", RabbitConfig.RISK_OUTPUT_QUEUE);

        } catch (Exception e) {
            log.error("Failed to process analysis for request {}", request.getRequestId(), e);
        }
    }
}
