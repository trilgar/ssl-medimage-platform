package org.trilgar.medimage.ssl.patient.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "examinations")
@Data
@NoArgsConstructor
public class Examination {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnore
    private Patient patient;

    private String modality;
    private String doctorNotes;

    private Double riskScore;
    private String diagnosis;
    private Boolean isCritical;
    private UUID imagingStorageId;

    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
