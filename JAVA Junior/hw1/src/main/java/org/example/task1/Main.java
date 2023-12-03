package org.example.task1;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(2, 3, 15, 15, 6, 8, 14, 21);
        double average = list.stream()
                .filter(num -> num % 2 == 0)
                .mapToInt(num->num)
                .average()
                .orElse(0.0);
        System.out.println(average);
    }
}