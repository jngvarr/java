//package ru.gb;

public class Cat {

    private String name;
    private int appetite;
    private volatile boolean satiety = false;

    public Cat(String name) {
        this(name, 10);
//        this.name = name;
//        this.appetite = 10;
//        satiety = false;
    }

    public Cat(String name, int appetite) {
        this.name = name;
        this.appetite = appetite;
//        satiety = false;

        Thread backgroundSatietyManagement = new Thread(() -> {
            while (true) {
                satiety = false;
                try {
                    Thread.sleep(5 * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        backgroundSatietyManagement.setDaemon(true);
        backgroundSatietyManagement.start();
    }

    public void eat(Plate plate) {
        if (!satiety) {
            satiety = plate.decreaseFood(appetite);
        }
    }

    public void makeHungry() {
        satiety = false;
    }

    @Override
    public String toString() {
        return name + "{appetite=" + appetite + ", satiety=" + satiety + "}";
    }
}
