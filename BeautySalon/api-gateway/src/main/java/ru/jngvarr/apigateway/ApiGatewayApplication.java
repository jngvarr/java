package ru.jngvarr.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("clients", r -> r.path("/clients/**")
                        .uri("http://localhost:8081/"))
                .route("services", r -> r.path("/services/**")
                        .uri("http://localhost:8082/"))
                .route("storage", r -> r.path("/storage/**")
                        .uri("http://localhost:8083/"))
                .route("storage", r -> r.path("/staff/**")
                        .uri("http://localhost:8084/"))
                .route("appointment", r -> r.path("/visits/**")
                        .uri("http://localhost:8085/"))
                .route("eserver", r -> r.path("/eureka/**")
                        .uri("http://localhost:8761/")).build();
    }
}
