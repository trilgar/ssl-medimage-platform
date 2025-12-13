package org.trilgar.medimage.ssl.patient.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateExaminationRequest {
    private UUID patientId;
    private String fullName;
    private String email;
    private LocalDate dob;
    private String modality;
    private String notes;
    private Boolean isUrgent;
}
