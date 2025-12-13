package org.trilgar.medimage.ssl.patient.service;

import org.trilgar.medimage.ssl.model.PatientExaminationRequest;
import org.trilgar.medimage.ssl.model.RiskAssessmentResult;
import org.trilgar.medimage.ssl.patient.entity.Examination;
import org.trilgar.medimage.ssl.patient.entity.Patient;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trilgar.medimage.ssl.patient.client.ImagingServiceClient;
import org.trilgar.medimage.ssl.patient.repository.ExaminationRepository;
import org.trilgar.medimage.ssl.patient.repository.PatientRepository;
import org.trilgar.medimage.ssl.patient.service.api.PatientService;
import org.trilgar.medimage.ssl.s3.api.S3StorageService;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final ExaminationRepository examinationRepository;
    private final RabbitTemplate rabbitTemplate;

    private final S3StorageService s3Service;
    private final ImagingServiceClient imagingClient;

    @Transactional
    @Override
    public Examination initiateExamination(Patient patient, String modality, String notes, boolean isUrgent) {
        if (patient.getId() == null) {
            patient = patientRepository.save(patient);
        }

        Examination exam = new Examination();
        exam.setId(UUID.randomUUID());
        exam.setPatient(patient);
        exam.setModality(modality);
        exam.setDoctorNotes(notes);
        exam.setStatus("REQUESTED");
        exam.setCreatedAt(LocalDateTime.now());

        examinationRepository.save(exam);

        PatientExaminationRequest request = new PatientExaminationRequest(
                exam.getId(),
                patient.getId(),
                patient.getFullName(),
                modality,
                notes,
                LocalDateTime.now(),
                isUrgent
        );

        rabbitTemplate.convertAndSend("examination_requests_queue", request);

        log.info("Examination initiated for patient {}. Request ID: {}", patient.getFullName(), exam.getId());
        return exam;
    }

    @Transactional
    @Override
    public void processAnalysisResult(RiskAssessmentResult result) {
        log.info("Processing analysis result for Request {}", result.getRequestId());

        Examination exam = examinationRepository.findById(result.getRequestId())
                .orElseThrow(() -> new RuntimeException("Examination not found"));

        exam.setRiskScore(result.getRiskScore());
        exam.setDiagnosis(result.getDiagnosisLabel());
        exam.setIsCritical(result.isCritical());
        exam.setCompletedAt(LocalDateTime.now());
        exam.setStatus("COMPLETED");

        if (result.getS3ObjectKey() != null) {
            byte[] imageData = s3Service.downloadImage(result.getS3ObjectKey());

            UUID pacsId = imagingClient.archiveImage(
                    result.getPatientId(),
                    imageData,
                    exam.getModality()
            );
            exam.setImagingStorageId(pacsId);
        }

        examinationRepository.save(exam);
        log.info("Examination {} completed and archived.", exam.getId());

        if (result.isCritical()) {
            log.warn("CRITICAL RESULT! Notification should be sent to doctor.");
        }
    }
}
