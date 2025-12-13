package org.trilgar.medimage.ssl.patient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.trilgar.medimage.ssl.s3.config.CommonS3Config;

@SpringBootApplication
@Import(CommonS3Config.class)
public class PatientServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }
}
