package org.trilgar.medimage.ssl.radiology.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.trilgar.medimage.ssl.model.PatientExaminationRequest;
import org.trilgar.medimage.ssl.radiology.service.api.ExaminationTaskService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExaminationRequestListener {
    private final ExaminationTaskService examinationTaskService;

    @RabbitListener(queues = "examination_requests_queue")
    public void handleDoctorRequest(PatientExaminationRequest request) {
        log.info("Received examination request for patient: {}", request.getPatientId());
        examinationTaskService.createTask(request);
        log.info("Task created. Radiologist can now see it.");
    }
}
