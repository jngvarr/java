package ru.gb.hw;

import ru.gb.hw.Animal;

import java.awt.*;

public class Cat extends Animal {
    String name;
    Color color;

    public Cat(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void toMeow() {
        System.out.println("Мяу-мяу!");
    }
}
