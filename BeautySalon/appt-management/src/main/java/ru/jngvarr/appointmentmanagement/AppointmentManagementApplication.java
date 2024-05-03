package ru.jngvarr.appointmentmanagement;

import feign_clients.ClientFeignClient;
import feign_clients.ServiceFeignClient;
import feign_clients.StaffFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@EnableFeignClients(basePackages = {"feign_clients"})
@SpringBootApplication
public class AppointmentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentManagementApplication.class, args);
    }
}