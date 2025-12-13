package org.trilgar.medimage.ssl.imaging.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trilgar.medimage.ssl.imaging.entity.ImageMetadata;
import org.trilgar.medimage.ssl.imaging.repository.ImageRepository;
import org.trilgar.medimage.ssl.imaging.service.api.ImagingService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImagingServiceImpl implements ImagingService {

    private final ImageRepository imageRepository;

    @Transactional
    @Override
    public ImageMetadata saveImage(UUID patientId, byte[] imageData, String modality) {
        ImageMetadata metadata = new ImageMetadata();
        metadata.setPatientId(patientId);
        metadata.setData(imageData);
        metadata.setModality(modality);
        metadata.setFormat("PNG");
        return imageRepository.save(metadata);
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] getImageData(UUID imageId) {
        return imageRepository.findById(imageId)
                .map(ImageMetadata::getData)
                .orElseThrow(() -> new RuntimeException("Image not found"));
    }

    @Override
    public List<ImageMetadata> getImagesByPatient(UUID patientId) {
        return imageRepository.findAllByPatientId(patientId);
    }
}