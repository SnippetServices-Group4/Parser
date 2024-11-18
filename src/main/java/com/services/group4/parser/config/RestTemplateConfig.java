package com.services.group4.parser.config;

import com.services.group4.parser.CorrelationIdInterceptor;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    CorrelationIdInterceptor correlationIdInterceptor = new CorrelationIdInterceptor();
    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
    interceptors.add(correlationIdInterceptor);
    restTemplate.setInterceptors(interceptors);

    return new RestTemplate();
  }
}
