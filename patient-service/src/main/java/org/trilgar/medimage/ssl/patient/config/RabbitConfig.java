package org.trilgar.medimage.ssl.patient.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String RISK_QUEUE = "risk_assessment_queue";
    public static final String EXAM_REQUEST_QUEUE = "examination_requests_queue";

    @Bean
    public Queue riskQueue() {
        return new Queue(RISK_QUEUE, true);
    }

    @Bean
    public Queue examRequestQueue() {
        return new Queue(EXAM_REQUEST_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
