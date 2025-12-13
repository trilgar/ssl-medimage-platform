package org.trilgar.medimage.ssl.radiology.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trilgar.medimage.ssl.PatientExaminationRequest;
import org.trilgar.medimage.ssl.radiology.entity.ExaminationTask;
import org.trilgar.medimage.ssl.radiology.repository.ExaminationTaskRepository;
import org.trilgar.medimage.ssl.radiology.service.api.ExaminationTaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExaminationTaskServiceImpl implements ExaminationTaskService {
    private final ExaminationTaskRepository repository;

    @Override
    public List<ExaminationTask> getPendingTasks() {
        return repository.findAllByStatusOrderByCreatedAtAsc(ExaminationTask.TaskStatus.PENDING);
    }

    @Override
    public ExaminationTask getTaskById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
    }

    @Override
    @Transactional
    public ExaminationTask createTask(PatientExaminationRequest request) {
        log.info("Creating new examination task for patient: {}", request.getPatientId());

        ExaminationTask task = new ExaminationTask();
        task.setId(request.getRequestId());
        task.setPatientId(request.getPatientId());
        task.setExpectedModality(request.getExaminationType());
        task.setPatientFullName(request.getPatientFullName());

        task.setStatus(ExaminationTask.TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setDoctorNotes(request.getDoctorNotes());
        task.setUrgent(request.isUrgent());
        task.setRequestedAt(request.getRequestedAt());

        return repository.save(task);
    }

    @Override
    @Transactional
    public ExaminationTask completeTask(UUID patientId, String resultS3Key) {
        ExaminationTask task = repository.findFirstByPatientIdAndStatus(
                patientId,
                ExaminationTask.TaskStatus.PENDING
        ).orElseThrow(() -> new RuntimeException("No pending task found for patient " + patientId));

        task.setStatus(ExaminationTask.TaskStatus.COMPLETED);
        task.setS3KeyResult(resultS3Key);
        task.setCompletedAt(LocalDateTime.now());

        log.info("Task {} completed.", task.getId());
        return repository.save(task);
    }


    @Override
    @Transactional
    public void cancelTask(UUID taskId) {
        ExaminationTask task = getTaskById(taskId);
        task.setStatus(ExaminationTask.TaskStatus.CANCELLED);
        task.setCompletedAt(LocalDateTime.now()); // Дата скасування
        repository.save(task);
        log.info("Task {} cancelled.", taskId);
    }
}
