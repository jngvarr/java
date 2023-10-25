package ru.gb.task3;

import com.sun.jdi.Value;

import java.util.Arrays;
import java.util.Random;

public class Pair<F, S> {
    F first;
    S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "First value = " + first + "\nSecond value = "+ second;
    }

    public static void main(String[] args) {
        Pair<Integer, Float> pair = new Pair<>(12, new Random().nextFloat());
        Pair<Double, String> pair2 = new Pair<>(12.4,"string" );
        System.out.println(pair);
        System.out.println(pair2);
    }
}
