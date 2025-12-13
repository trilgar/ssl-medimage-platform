package org.trilgar.medimage.ssl.radiology.service.api;

import java.util.UUID;

public interface RadiologyService {
    void processScan(UUID patientId, byte[] fileData, String modality);
}
