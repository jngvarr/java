package ru.gb.lesson4.hw;

import java.util.*;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;

public class Box<F extends Fruit> implements Iterable<F> {

    private final List<F> contents = new ArrayList<>();

    public void add(F fruit) {
        // добавляем фрукт в коробку
        contents.add(fruit);
    }

    public int getWeight() {
        // TODO: 13.03.2023 Сумма весов всех фруктов
        int result = 0;
        for (F f : contents) {
            result += f.getWeight();
        }
        return result;
    }

    // пересыпаем фрукты отсюда в target
    public void moveTo2(Box<? super F> target) {
        for (F f: contents) {
            target.add(f);
        }
        contents.clear();
    }

    public void moveTo(Box<? super F> target) {
        Iterator<F> i = contents.iterator();
        while (i.hasNext()) {
            target.add(i.next());
        }
        i.remove();
    }


    @Override
    public Iterator<F> iterator() {
        return contents.iterator();
    }

    @Override
    public void forEach(Consumer<? super F> action) {
        contents.forEach(action);
    }

    @Override
    public Spliterator<F> spliterator() {
        return contents.spliterator();
    }
}


