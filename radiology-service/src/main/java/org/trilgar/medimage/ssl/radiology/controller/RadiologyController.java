package org.trilgar.medimage.ssl.radiology.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.trilgar.medimage.ssl.radiology.service.api.RadiologyService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/radiology")
@RequiredArgsConstructor
public class RadiologyController {
    private final RadiologyService radiologyService;

    @PostMapping("/scan")
    public ResponseEntity<String> performScan(
            @RequestParam("patientId") UUID patientId,
            @RequestParam("modality") String modality,
            @RequestParam("file") MultipartFile file) {
        try {
            radiologyService.processScan(patientId, file.getBytes(), modality);

            return ResponseEntity.ok("Scan uploaded successfully. Analysis started.");

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File processing error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
