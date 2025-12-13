package org.trilgar.medimage.ssl.analytics.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String ANALYSIS_INPUT_QUEUE = "analysis_queue";

    public static final String RISK_OUTPUT_QUEUE = "risk_assessment_queue";

    @Bean
    public Queue riskOutputQueue() {
        return new Queue(RISK_OUTPUT_QUEUE, true);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
