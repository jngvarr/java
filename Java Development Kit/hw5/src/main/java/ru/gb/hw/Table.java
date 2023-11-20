package ru.gb.hw;

public interface Table {
    void seat(Eater eater);

    boolean pickUp(Fork fork);

    void putDown(Fork fork);

}
