package ru.gb.hw;

public class Fork {
    private final int number;

    public Fork(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fork fork = (Fork) o;

        return number == fork.number;
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public String toString() {
        return "ForkImpl{" +
                "number=" + number +
                '}';
    }
}