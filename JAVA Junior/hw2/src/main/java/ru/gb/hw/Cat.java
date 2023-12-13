package ru.gb.hw;

import ru.gb.hw.Animal;

import java.awt.*;

public class Cat extends Animal {
    private String name;
    private Color color;

    public Cat(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String toMeow() {
       return "Мяу-мяу!";
    }
}
