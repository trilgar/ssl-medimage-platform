package org.trilgar.medimage.ssl.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.trilgar.medimage.ssl.s3.config.CommonS3Config;

@SpringBootApplication
@Import(CommonS3Config.class)
public class AnalyticalServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticalServiceApplication.class, args);
    }
}
