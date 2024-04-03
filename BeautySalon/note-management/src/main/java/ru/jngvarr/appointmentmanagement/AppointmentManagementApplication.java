package ru.jngvarr.appointmentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class AppointmentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentManagementApplication.class, args);
    }

}
