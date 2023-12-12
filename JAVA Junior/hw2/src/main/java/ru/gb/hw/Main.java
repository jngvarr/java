package ru.gb.hw;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Dog("Шарик", 10));
        animals.add(new Dog("Бобик", 15));
        animals.add(new Dog("Тузик", 5));
        animals.add(new Cat("Мурка", new Color(10, 20, 30)));
        animals.add(new Cat("Машка", new Color(20, 100, 200)));
        animals.add(new Cat("Мурка", new Color(100, 60, 50)));

        for (Animal animal : animals) {
            System.out.println("Класс: " + animal.getClass().getSimpleName());
            Field[] classFields = animal.getClass().getDeclaredFields();
            Method[] classMethods = animal.getClass().getDeclaredMethods();
            for (Field field : classFields) {
                System.out.printf("%s: %s\n", field.getName(), field.get(animal));
            }
            for (Method method : classMethods) {
                System.out.printf(method.invoke(animal)+"\n");
//                System.out.printf(method.getName() + " :" + method.invoke(animal)+"\n");
            }
        }

    }
}