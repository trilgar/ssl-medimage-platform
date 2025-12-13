package org.trilgar.medimage.ssl.radiology.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.trilgar.medimage.ssl.model.ImageAnalysisRequest;
import org.trilgar.medimage.ssl.radiology.config.RabbitConfig;
import org.trilgar.medimage.ssl.radiology.entity.ExaminationTask;
import org.trilgar.medimage.ssl.radiology.service.api.ExaminationTaskService;
import org.trilgar.medimage.ssl.radiology.service.api.RadiologyService;
import org.trilgar.medimage.ssl.s3.api.S3StorageService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RadiologyServiceImpl implements RadiologyService {
    private final S3StorageService storageService;
    private final RabbitTemplate rabbitTemplate;
    private final ExaminationTaskService taskService;

    @Override
    public void processScan(UUID patientId, byte[] fileData, String modality) {
        String s3Key = storageService.upload(fileData, "png");

        ExaminationTask completedTask = taskService.completeTask(patientId, s3Key);

        if (!completedTask.getExpectedModality().equals(modality)) {
            log.warn("Modality mismatch: Expected {}, Got {}", completedTask.getExpectedModality(), modality);
        }

        ImageAnalysisRequest request = new ImageAnalysisRequest(
                completedTask.getId(),
                patientId,
                s3Key,
                modality
        );

        rabbitTemplate.convertAndSend(RabbitConfig.ANALYSIS_QUEUE, request);

        log.info("Scan workflow completed for task {}", completedTask.getId());
    }
}
