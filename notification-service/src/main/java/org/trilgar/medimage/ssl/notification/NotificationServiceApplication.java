package org.trilgar.medimage.ssl.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.trilgar.medimage.ssl.s3.config.CommonS3Config;

@SpringBootApplication
@Import(CommonS3Config.class)
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
