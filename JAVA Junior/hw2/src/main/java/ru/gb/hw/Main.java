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
        animals.add(new Cat("Кися", new Color(100, 60, 50)));

        for (Animal animal : animals) {
            printInfo(animal);
        }
    }
    private static <T> void printInfo(T obj) throws IllegalAccessException, InvocationTargetException {
        Class<?> objClass = obj.getClass();
        System.out.println("Класс: " + objClass.getSimpleName());
        Field[] classFields = objClass.getDeclaredFields();
        Method[] classMethods = objClass.getDeclaredMethods();
        for (Field field : classFields) {
            field.setAccessible(true);
            System.out.printf("%s: %s\n", field.getName(), field.get(obj));
        }
        try {
            Method makeSoundMethod = obj.getClass().getMethod("makeSound");
            makeSoundMethod.invoke(obj.getClass().getSimpleName());
        } catch (Exception e) {
            System.out.println("Класс " + obj.getClass().getSimpleName() + " не имеет метода makeSound().\nЗато есть такой: ");
            for (Method method : classMethods) {
                System.out.println(method.getName() + ": " + method.invoke(obj));
            }
            System.out.println();
        }
    }

}
