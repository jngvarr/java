package ru.gb.hw;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String[] philosopherNames = {"Диоген", "Платон", "Сократ", "Эпикур", "Аристотель"};

    public static void main(String[] args) {
        Table table = new TableImpl(philosopherNames.length);
        List<Philosopher> philosophers = new ArrayList<>(philosopherNames.length);
        for (String name : philosopherNames) {
            Philosopher philosopher = new Philosopher(name, 3);
            philosophers.add(philosopher);
            table.seat(philosopher);
        }

        for (Philosopher philosopher : philosophers) {
            philosopher.start();
        }
    }
}