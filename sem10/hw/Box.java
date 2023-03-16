package ru.gb.lesson4.hw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Box<T extends Fruit> implements Iterable{
    int boxWeight;
//    private List<T> fruitBoxesList = new ArrayList<>();

    /* TODO: Тут должен быть дженерик */
    public void add(T fruit) {
        // добавляем фрукт в коробку
//        fruitBoxesList.add(fruit);
        this.boxWeight+= fruit.getWeight();
    }

    public int getWeight() {
        // TODO: 13.03.2023 Сумма весов всех фруктов
        return boxWeight;
    }

    //
    public void moveTo(Box<Apple> target) {
// пересыпаем фрукты отсюда в target
        int tempBoxWeight = this.boxWeight;
        target.boxWeight+=this.boxWeight;
        this.boxWeight-=tempBoxWeight;
    }

    @Override
    public Iterator <T> iterator() {
        return 0;
    }
}
