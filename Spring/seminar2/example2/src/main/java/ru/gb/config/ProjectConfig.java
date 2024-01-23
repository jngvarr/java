package ru.gb.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.gb.domain.Car;
import ru.gb.domain.Engine;

@Configuration
@ComponentScan(basePackages = "ru.gb.domain")
public class ProjectConfig {
    //    @Bean
//    Engine engine(){
//        Engine eng = new Engine();
//        return eng;
//    }
//    @Bean
//    Car car(){
//        Car objCar = new Car();
    @Bean
    Car car(@Qualifier("engine2") Engine eng) {
        Car objCar = new Car(eng);
// //       objCar.setCarEngine(engine());
        objCar.setMade("Lada");
        objCar.setModel("2101");
        return objCar;
    }

    @Bean
    Car car2(@Qualifier("engine1") Engine eng) {
        Car objCar = new Car(eng);
//        objCar.setCarEngine(engine());
        objCar.setMade("Lada");
        objCar.setModel("2101");
        return objCar;
    }

}
