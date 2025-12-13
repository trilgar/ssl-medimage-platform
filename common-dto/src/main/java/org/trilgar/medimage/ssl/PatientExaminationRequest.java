package org.trilgar.medimage.ssl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientExaminationRequest {

    private UUID requestId;
    
    private UUID patientId;
    
    private String patientFullName;
    
    private String examinationType;
    
    private String doctorNotes;
    
    private LocalDateTime requestedAt;
    
    private boolean isUrgent;
}