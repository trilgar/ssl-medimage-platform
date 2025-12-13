package org.trilgar.medimage.ssl.patient.service.api;

import org.trilgar.medimage.ssl.model.RiskAssessmentResult;
import org.trilgar.medimage.ssl.patient.entity.Examination;
import org.trilgar.medimage.ssl.patient.entity.Patient;

import java.util.UUID;

public interface PatientService {
    Examination initiateExamination(Patient patient, String modality, String notes, boolean isUrgent);
    void processAnalysisResult(RiskAssessmentResult result);
}
