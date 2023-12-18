package ru.gb.lesson3.iter;

import java.util.ArrayList;
import java.util.List;

public class Box {

    public static void main(String[] args) {
        Box box = new Box();
//        for (Object t: box) {
//
//        }

    }
    // depth-first search
    // breath-first search

    private int height;
    private int length;
    private int width;

    private List<Object> things;

    public Box() {
        this.things = new ArrayList<>();
    }

    public void add(Object t) {
        //
        things.add(t);
    }


}
