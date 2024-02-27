package ru.gb.hw12;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class Hw5Application {

	public static void main(String[] args) {
		SpringApplication.run(Hw5Application.class, args);
	}

}
