package net.doha.microservice_planaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroServicePlanActionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroServicePlanActionApplication.class, args);
    }

}
