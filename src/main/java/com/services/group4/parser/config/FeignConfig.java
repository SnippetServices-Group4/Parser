package com.services.group4.parser.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    private static final String CORRELATION_ID_KEY = "correlation-id";

    @Bean
    public RequestInterceptor correlationIdRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                String correlationId = MDC.get(CORRELATION_ID_KEY);
                if (correlationId != null) {
                    template.header("X-Correlation-Id", correlationId);
                }
            }
        };
    }
}
