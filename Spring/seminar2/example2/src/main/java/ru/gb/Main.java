package ru.gb;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gb.config.ProjectConfig;
import ru.gb.domain.Car;
import ru.gb.domain.Engine;

public class Main {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(ProjectConfig.class);

        Car c = context.getBean(Car.class);
        Engine e = context.getBean(Engine.class);
        System.out.println(c);
        System.out.println(e);
//        Car c2= context.getBean(Car.class);
//        System.out.println(c2);
    }
}