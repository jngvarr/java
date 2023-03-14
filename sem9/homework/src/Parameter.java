package ru.gb.homework.src;

public abstract class Parameter {
    public String name;

    public Parameter(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
