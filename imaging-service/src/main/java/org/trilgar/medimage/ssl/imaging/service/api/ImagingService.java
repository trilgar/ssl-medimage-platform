package org.trilgar.medimage.ssl.imaging.service.api;

import org.trilgar.medimage.ssl.imaging.entity.ImageMetadata;

import java.util.List;
import java.util.UUID;

public interface ImagingService {
    ImageMetadata saveImage(UUID patientId, byte[] imageData, String modality);

    byte[] getImageData(UUID imageId);

    List<ImageMetadata> getImagesByPatient(UUID patientId);
}
