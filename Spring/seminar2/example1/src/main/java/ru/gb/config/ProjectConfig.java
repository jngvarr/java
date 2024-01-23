package ru.gb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.gb.domain.Car;

@Configuration
public class ProjectConfig {
    @Bean("BMW")
    Car car(){
        Car objCar = new Car();
        objCar.setMade("BMW");
        objCar.setModel("X1");
        return objCar;
    }
    @Bean(name = "Lada")
    Car car1(){
        Car objCar = new Car();
        objCar.setMade("Lada");
        objCar.setModel("2101");
        return objCar;
    }
    @Bean
    @Primary
    Car car2(){
        Car objCar = new Car();
        objCar.setMade("HAVAL");
        objCar.setModel("H7");
        return objCar;
    }

    @Bean
    String hello(){
        return "hello";
    }
    @Bean
    Integer ten(){
        return 10;
    }

}
