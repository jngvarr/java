package ru.gb.task1;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    public static  <T extends Number> double sum(T num, T num2) {
        return num.doubleValue() + num2.doubleValue();
    }

    public static  <T extends Number> double multiply(T num, T num2) {
        return num.doubleValue() * num2.doubleValue();
    }

    public static  <T extends Number> double divide(T num, T num2) {
        if (num2.doubleValue() != 0) return num.doubleValue() / num2.doubleValue();
        else throw new ArithmeticException("Деление на ноль запрещено");
    }

    public static <T extends Number> double subtract(T num, T num2) {
        return num.doubleValue() - num2.doubleValue();
    }

}
