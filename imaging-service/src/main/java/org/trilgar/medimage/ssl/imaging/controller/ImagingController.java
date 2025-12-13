package org.trilgar.medimage.ssl.imaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.trilgar.medimage.ssl.imaging.entity.ImageMetadata;
import org.trilgar.medimage.ssl.imaging.service.api.ImagingService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImagingController {

    private final ImagingService imagingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageMetadata uploadImage(
            @RequestParam("patientId") UUID patientId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("modality") String modality) throws IOException {
        return imagingService.saveImage(
                patientId,
                file.getBytes(),
                modality
        );
    }

    @GetMapping(value = "/{id}/content", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImageContent(@PathVariable UUID id) {
        return imagingService.getImageData(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<ImageMetadata> getPatientImages(@PathVariable UUID patientId) {
        return imagingService.getImagesByPatient(patientId);
    }
}
