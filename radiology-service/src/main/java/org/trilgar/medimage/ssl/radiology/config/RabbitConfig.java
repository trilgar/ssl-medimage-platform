package org.trilgar.medimage.ssl.radiology.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    public static final String ANALYSIS_QUEUE = "analysis_queue";
    public static final String EXAMINATION_REQUESTS_QUEUE = "examination_requests_queue";

    @Bean
    public Queue analysisQueue() {
        return new Queue(ANALYSIS_QUEUE, true);
    }

    @Bean
    public Queue examinationRequestsQueue() {
        return new Queue(EXAMINATION_REQUESTS_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
