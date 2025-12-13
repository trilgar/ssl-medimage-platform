package org.trilgar.medimage.ssl.patient.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImagingServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.imaging.url}")
    private String imagingServiceUrl;

    public UUID archiveImage(UUID patientId, byte[] imageData, String modality) {
        try {
            log.info("Archiving image for patient {} to ImagingService...", patientId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource fileResource = new ByteArrayResource(imageData) {
                @Override
                public String getFilename() {
                    return "archived-scan.png";
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            body.add("patientId", patientId.toString());
            body.add("modality", modality);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ImageResponse response = restTemplate.postForObject(imagingServiceUrl, requestEntity, ImageResponse.class);

            if (response != null) {
                log.info("Image archived successfully. Image ID: {}, PatientId: {}", response.id(), response.patientId());
                return response.id();
            }
        } catch (Exception e) {
            log.error("Failed to archive image in ImagingService", e);
        }
        return null;
    }

    private record ImageResponse(UUID id, String patientId, String modality) {
    }
}
