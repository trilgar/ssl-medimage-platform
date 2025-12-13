package org.trilgar.medimage.ssl.imaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.trilgar.medimage.ssl.s3.config.CommonS3Config;

@SpringBootApplication
public class ImagingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImagingServiceApplication.class, args);
    }
}
