package org.trilgar.medimage.ssl.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.trilgar.medimage.ssl.patient.entity.Examination;

import java.util.UUID;

public interface ExaminationRepository extends JpaRepository<Examination, UUID> {
}
