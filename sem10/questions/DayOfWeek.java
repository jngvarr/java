package ru.gb.lesson4.questions;

public enum DayOfWeek {

    MONDAY(1),
    TUESDAY(2);

    private final int x;

    DayOfWeek(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return String.valueOf(x);
    }
}
