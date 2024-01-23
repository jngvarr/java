package ru.gb;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gb.config.ProjectConfig;
import ru.gb.domain.Car;
import ru.gb.domain.DieselEngine;

public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(ProjectConfig.class);
        Car c = context.getBean(Car.class);
        c.go();
        System.out.println("----------------------------------");
        DieselEngine dEn = context.getBean(DieselEngine.class);
    }
}