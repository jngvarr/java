package ru.gb.hw;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Philosopher extends Thread {
    int ateThrice;
    int seaterNumber;
    private final String name;
    private boolean hungry;
    private boolean pondered;


    Fork leftFork;
    Fork rightFork;

    public Philosopher(String name) {
        this.name = name;
        pondered = true;
        hungry = true;
    }

    public boolean isHungry() {
        return hungry;
    }

    public boolean isPondered() { // проверка, поразмышлял ли философ
        return pondered;
    }

    public void setHungry(boolean hungry) { // проверка, поел ли философ (голоден или нет)
        this.hungry = hungry;
    }

    public void setPondered(boolean pondered) {
        this.pondered = pondered;
    }

    public void takeAFork(Fork fork) {
        System.out.println(this.name + " берёт вилку " + fork.getForkNumber() + " (вилка уже взята: " + fork.isTaken()+")");
        fork.setFork(true);
    }

    public void putDownAFork(Fork fork) {
        System.out.println(this.name + " положил вилку " + fork.getForkNumber());
        fork.setFork(false);
    }

    public void toEat() throws InterruptedException {
        int kostyl = 0;
        seaterNumber = Table.seaters.indexOf(this);
        leftFork = Table.forks.get(seaterNumber);
        if (seaterNumber == 0) kostyl = Table.seaters.size();
        rightFork = Table.forks.get(seaterNumber - 1 + kostyl);
        if (isHungry() && isPondered()) {
            if (!leftFork.isTaken()) {
                takeAFork(leftFork);
                if (!rightFork.isTaken()) {
                    takeAFork(rightFork);
                    sleep(100);
                    setHungry(false);
                    setPondered(false);
                    System.out.println(name + " поел!");
                    ateThrice++;
                }
            }
        } else {
            toPonder();
        }
        if (leftFork.isTaken()) putDownAFork(leftFork);
        if (rightFork.isTaken()) putDownAFork(rightFork);
    }

    @Override
    public void run() {
        while (ateThrice < 3) {
            try {
                toEat();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(this.name + " наелся!");
    }

    public void toPonder() throws InterruptedException {
        sleep(1000);
        setPondered(true);
        setHungry(true);
        System.out.println(name + " поразмышлял!");
    }
}
