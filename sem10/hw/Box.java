package ru.gb.lesson4.hw;

import java.util.*;

public class Box<F extends Fruit> implements Iterable<F> {

    int boxWeight;
    //    public Box(){
//
//    }
    private List<F> fruitBoxesList = new ArrayList<>();

    /* TODO: Тут должен быть дженерик */
    public void add(F fruit) {
        // добавляем фрукт в коробку
        fruitBoxesList.add(fruit);
        this.boxWeight += fruit.getWeight(1);
    }

    public int getWeight() {
        // TODO: 13.03.2023 Сумма весов всех фруктов
        return boxWeight;
    }

    //
    public void moveTo(Box<Apple> target) {
// пересыпаем фрукты отсюда в target
        int tempBoxWeight = this.boxWeight;
        target.boxWeight += this.boxWeight;
        this.boxWeight -= tempBoxWeight;
    }

    public boolean hasNext() {
        return boxWeight != 0;
    }

    public String next() {
        return "";
    }

    @Override
    public Iterator<F> iterator() {
        hasNext();
        return boxWeight--;
    }
}


