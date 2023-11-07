package ru.gb.hw;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Table {
    String[] philosopherNames = {"Диоген", "Платон", "Сократ", "Эпикур", "Аристотель"};
    List<Seat> seats;
    Map<Seat, Eater> eaters;
    Map<Fork, Eater> pickedUpForks;

    public Table() {
        for (Seat seat : seats) {
            eaters.put(seat, new Philosopher(philosopherNames[seat.getNumber()],3));
        }
    }

    void seat(Eater eater) {
    }

    void pickUp(Fork fork) {
    }

    void putDown(Fork fork) {
    }

    void unseat(Eater eater) {
    }
}
