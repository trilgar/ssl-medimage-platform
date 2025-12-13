package org.trilgar.medimage.ssl.patient.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trilgar.medimage.ssl.patient.entity.Patient;
import org.trilgar.medimage.ssl.patient.model.CreateExaminationRequest;
import org.trilgar.medimage.ssl.patient.service.api.PatientService;

import java.util.UUID;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientRecordController {

    private final PatientService patientService;

    @PostMapping("/examine")
    public ResponseEntity<String> createExamination(@RequestBody CreateExaminationRequest request) {

        Patient patient = new Patient();
        if (request.getPatientId() != null) patient.setId(request.getPatientId());
        patient.setFullName(request.getFullName());
        patient.setEmail(request.getEmail());
        patient.setDateOfBirth(request.getDob());

        UUID examId = patientService.initiateExamination(patient, request.getModality(), request.getNotes(), request.getIsUrgent());

        return ResponseEntity.ok("Examination created. ID: " + examId);
    }
}
