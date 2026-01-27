package net.doha.microservice_audit.config;

import net.doha.microservice_audit.service.NonConformiteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public NonConformiteService nonConformiteService() {
        return new NonConformiteService();
    }

}
