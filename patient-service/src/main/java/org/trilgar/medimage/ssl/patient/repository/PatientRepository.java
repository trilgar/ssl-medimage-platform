package org.trilgar.medimage.ssl.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trilgar.medimage.ssl.patient.entity.Patient;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
}
