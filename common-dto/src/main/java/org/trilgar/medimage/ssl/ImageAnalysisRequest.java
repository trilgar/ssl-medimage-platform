package org.trilgar.medimage.ssl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageAnalysisRequest {
    private UUID requestId;
    private UUID patientId;

    private String s3ObjectKey;

    private String modality;
}