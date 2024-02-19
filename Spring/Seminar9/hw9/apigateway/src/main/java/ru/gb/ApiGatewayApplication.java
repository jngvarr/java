package ru.gb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

		public static void main(String[] args) {
			SpringApplication.run(ApiGatewayApplication.class, args);
		}

		@Bean
		public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
			return builder.routes()
//					.route("web", r -> r.path("/web/**")
//							.uri("http://localhost:8080/"))
					.route("storage", r -> r.path("/storage/**")
							.uri("http://localhost:8081/"))
					.route("payment", r -> r.path("/payment/**")
							.uri("http://localhost:8082/")).build();
		}
	}
