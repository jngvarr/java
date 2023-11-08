package ru.gb.hw;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableImpl implements Table {
    private final List<Seat> seats;
    private final Map<Eater, Seat> eaters = new LinkedHashMap<>();
    private final Map<Fork, Eater> pickedUpForks = new LinkedHashMap<>();

    public TableImpl(int size) {
        seats = new ArrayList<>(size);
        int forkCounter = 0;
        Fork left;
        Fork right;
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                left = new Fork(forkCounter++);
                right = new Fork(forkCounter++);
            } else if (i == size - 1) {
                left = seats.get(i - 1).getRight();
                right = seats.get(0).getLeft();
            } else {
                left = seats.get(i - 1).getRight();
                right = new Fork(forkCounter++);
            }
            seats.add(new Seat(i, left, right));
           // System.out.println(i + " " + left.getNumber() + " " + right.getNumber());
        }
    }

    @Override
    public synchronized void seat(Eater eater) {
        if (eaters.containsKey(eater)) {
            throw new IllegalStateException("Этот едок уже за столом.");
        }
        Seat empty = null;
        for (Seat seat : seats) {
            if (eaters.containsValue(seat)) {
                continue;
            }
            empty = seat;
            break;
        }
        if (empty == null) {
            throw new IllegalStateException("Свободных мест нет.");
        }
        eaters.put(eater, empty);
        eater.assign(this, empty.getLeft(), empty.getRight());
    }

    @Override
    public synchronized boolean pickUp(Fork fork) {
        Eater eater = faceControl();
        Seat seat = eaters.get(eater);
        if (seat == null) {
            throw new IllegalStateException("Данный едок не сидит за столом.");
        }
        if (seat.getLeft() != fork && seat.getRight() != fork) {
            throw new IllegalStateException("Эта вилка не принадлежит данному едоку.");
        }
        Eater holdingEater = pickedUpForks.get(fork);
        if (holdingEater == null) {
            pickedUpForks.put(fork, eater);
            return true;
        }
        return holdingEater == eater;
    }

    @Override
    public synchronized void putDown(Fork fork) {
        Eater eater = faceControl();
        Eater holdingEater = pickedUpForks.get(fork);
        if (holdingEater == null) {
            return;
        }
        if (holdingEater != eater) {
            throw new IllegalStateException("Это чужая вилка.");
        }
        pickedUpForks.remove(fork);
    }

    private Eater faceControl() {
        Thread current = Thread.currentThread();
        if (current instanceof Eater eater) {
            return eater;
        }
        throw new IllegalStateException("Неверный тип едока.");
    }

}
