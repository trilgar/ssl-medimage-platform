package org.trilgar.medimage.ssl.radiology.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trilgar.medimage.ssl.radiology.service.api.S3StorageService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@Slf4j
public class S3StorageServiceImpl implements S3StorageService {
    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageServiceImpl(S3Client s3Client,
                                @Value("${s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;

        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("Bucket '{}' already exists.", bucketName);
        } catch (NoSuchBucketException e) {
            log.info("Bucket '{}' not found. Creating...", bucketName);
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("Error checking/creating bucket '{}': {}", bucketName, e.getMessage());
            throw new RuntimeException("Could not initialize S3 storage", e);
        }
    }

    @Override
    public String upload(byte[] data, String extension) {
        String fileName = UUID.randomUUID() + "." + extension;

        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType("image/" + extension)
                .build();

        s3Client.putObject(putOb, RequestBody.fromBytes(data));

        log.info("Uploaded to І3: {}", fileName);
        return fileName;
    }
}
