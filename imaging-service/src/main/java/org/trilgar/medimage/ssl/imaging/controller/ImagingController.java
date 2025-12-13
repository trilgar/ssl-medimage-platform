package org.trilgar.medimage.ssl.imaging.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @GetMapping(value = "/{id}/content", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImageContent(@PathVariable("id") UUID id) {
        return imagingService.getImageData(id);
    }

    @Transactional(readOnly = true)
    @GetMapping("/patient/{patientId}")
    public List<ImageMetadata> getPatientImages(@PathVariable("patientId") UUID patientId) {
        return imagingService.getImagesByPatient(patientId);
    }
}
