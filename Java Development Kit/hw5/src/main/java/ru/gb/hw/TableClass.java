package ru.gb.hw;

import ru.gb.old_hw.Philosopher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TableClass {
    public static List<Philosopher> seaters = new ArrayList<>(5);

    public static List<Fork> forks = new ArrayList<>(5);
    public TableClass() {
        seaters.add(new Philosopher("Диоген"));
        seaters.add(new Philosopher("Платон"));
        seaters.add(new Philosopher("Сократ"));
        seaters.add(new Philosopher("Эпикур"));
        seaters.add(new Philosopher("Аристотель"));

        for (int i = 0; i < seaters.size(); i++) {
            forks.add(new Fork());
        }
    }

    public void allPhilosopherTryToEat() throws InterruptedException {

        for (Philosopher seater : seaters) {
            seater.start();
        }
    }
}
