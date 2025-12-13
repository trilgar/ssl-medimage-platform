package org.trilgar.medimage.ssl.radiology;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.trilgar.medimage.ssl.s3.config.CommonS3Config;

@SpringBootApplication
@Import(CommonS3Config.class)
public class RadiologyApplication {
    public static void main(String[] args) {
        SpringApplication.run(RadiologyApplication.class, args);
    }
}
