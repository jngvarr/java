package ru.gb;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gb.config.ProjectConfig;
import ru.gb.domain.Car;

import java.text.Annotation;

public class Main {
    public static void main(String[] args) {
//        Car bmw = new Car();
//        bmw.setMade("BMW");
//        bmw.setModel("X1");
//        System.out.println(bmw.getMade()+ " " + bmw.getModel());

        var context = new AnnotationConfigApplicationContext(ProjectConfig.class);

        Car simpleCar = context.getBean(Car.class);
        System.out.println(simpleCar.getMade() + " " + simpleCar.getModel());

        Car bmvCar = context.getBean("BMW", Car.class);
        Car ladaCar = context.getBean("Lada", Car.class);

        System.out.println(bmvCar.getMade()+" "+bmvCar.getModel());
        System.out.println(ladaCar.getMade()+" "+ladaCar.getModel());

        String s = context.getBean(String.class);
        System.out.println(s);

        Integer i = context.getBean(Integer.class);
        System.out.println(i);
    }
}