package ru.gb.jdk.two.online.samples;

public interface Bull {
    public static final int amount = 2; // поля интерфейсов всегда публичные

    //void walk() {
    default void walk() {
        System.out.println("Walks on +" + amount + "+ hooves");
    }

    void talk();

}
