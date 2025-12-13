package org.trilgar.medimage.ssl.radiology.service.api;

import org.trilgar.medimage.ssl.PatientExaminationRequest;
import org.trilgar.medimage.ssl.radiology.entity.ExaminationTask;

import java.util.List;
import java.util.UUID;

public interface ExaminationTaskService {
    List<ExaminationTask> getPendingTasks();
    ExaminationTask getTaskById(UUID id);
    ExaminationTask createTask(PatientExaminationRequest request);
    ExaminationTask completeTask(UUID patientId, String resultS3Key);
    void cancelTask(UUID taskId);
}
