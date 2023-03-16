package ru.gb.lesson4.generics;

import java.util.ArrayList;
import java.util.List;

// T Type, E element
public class NumberContainer<T extends Number> {

    private List<T> delegate;
    private T t1;

    public NumberContainer() {
        delegate = new ArrayList<>();

    }

    public void addNumber(T number) {
        delegate.add(number);
    }

    public int sum() {
        int sum = 0;
        for (T t : delegate) {
            sum += t.intValue();
        }
        return sum;
    }

}
