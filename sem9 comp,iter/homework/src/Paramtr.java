package ru.gb.homework.src;

public abstract class Paramtr {
    public String name;

    public Paramtr(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
