package org.trilgar.medimage.ssl.radiology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trilgar.medimage.ssl.radiology.entity.ExaminationTask;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExaminationTaskRepository extends JpaRepository<ExaminationTask, UUID> {
    Optional<ExaminationTask> findFirstByPatientIdAndStatus(UUID patientId, ExaminationTask.TaskStatus status);

    List<ExaminationTask> findAllByStatusOrderByCreatedAtAsc(ExaminationTask.TaskStatus status);
}
