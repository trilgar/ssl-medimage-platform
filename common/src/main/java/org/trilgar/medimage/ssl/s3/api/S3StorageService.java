package org.trilgar.medimage.ssl.s3.api;

public interface S3StorageService {
    String upload(byte[] data, String extension);
    byte[] downloadImage(String key);
}
