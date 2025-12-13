package org.trilgar.medimage.ssl.radiology.service.api;

public interface S3StorageService {
    String upload(byte[] data, String extension);
}
