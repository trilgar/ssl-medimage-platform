package org.trilgar.medimage.ssl.imaging.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.trilgar.medimage.ssl.imaging.entity.ImageMetadata;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<ImageMetadata, UUID> {
    List<ImageMetadata> findAllByPatientId(UUID patientId);
}
