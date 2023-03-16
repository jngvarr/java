package ru.gb.lesson4.hw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Box<T extends Fruit> {
    private List<T> fruitBoxesList = new ArrayList<>();

    /* TODO: Тут должен быть дженерик */
    public void add(T fruit) {
        // добавляем фрукт в коробку
        fruitBoxesList.add(fruit);
        System.out.println(Arrays.toString(fruitBoxesList));
               // getWeight());
    }

//    public int getWeight() {
//        // TODO: 13.03.2023 Сумма весов всех фруктов
//        return boxWeight;
//
//        public void moveTo (Box/* TODO: Тут должен быть дженерик */ target){
//            // пересыпаем фрукты отсюда в target
        }


//    }
//}