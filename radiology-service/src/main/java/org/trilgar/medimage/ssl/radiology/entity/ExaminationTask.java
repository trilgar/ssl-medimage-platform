package org.trilgar.medimage.ssl.radiology.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "examination_tasks")
@Data
@NoArgsConstructor
public class ExaminationTask {
    @Id
    private UUID id;

    private UUID patientId;
    private String expectedModality;
    private String patientFullName;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String s3KeyResult;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    private String doctorNotes;

    private LocalDateTime requestedAt;

    private boolean isUrgent;

    public enum TaskStatus {
        PENDING,
        COMPLETED,
        CANCELLED
    }
}
