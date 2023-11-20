package ru.gb.hw;

import java.util.Random;

public class Philosopher extends Thread implements Eater {
    private final String name;
    private final int timesToEat;
    private Fork leftFork;
    private Fork rightFork;
    private Table table;

    private static final Random RND = new Random();

    public Philosopher(String name, int timesToEat) {
        this.name = name;
        this.timesToEat = timesToEat;
    }


    @Override
    public void run() {
        int timesEaten = 0;
        do {
            if (toEat()) {
                timesEaten++;
            }
            if (timesEaten < 3) toPonder();
        } while (timesEaten < timesToEat);
        System.out.println(name + " наелся.");
        leftFork = null;
        rightFork = null;
    }

    private boolean toEat() {
        boolean eaten = false;
        if (table.pickUp(leftFork)) {
            System.out.println(name + " взял вилку " + leftFork.getNumber());
            if (table.pickUp(rightFork)) {
                System.out.println(name + " взял вилку " + rightFork.getNumber());
                pause(500, 1000);
                System.out.println(name + " поел.");
                eaten = true;
                table.putDown(rightFork);
                System.out.println(name + " положил вилку " + rightFork.getNumber());
            }
            table.putDown(leftFork);
            System.out.println(name + " положил вилку " + leftFork.getNumber());
        }
        return eaten;
    }

    public void toPonder() {
        pause(500, 1000);
        System.out.println(name + " поразмышлял!");
    }

    @SuppressWarnings("SameParameterValue")
    private static void pause(int fixed, int variable) {
        try {
            Thread.sleep(fixed + RND.nextInt(0, variable));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void assign(Table table, Fork left, Fork right) {
        this.table = table;
        leftFork = left;
        rightFork = right;
    }
}
