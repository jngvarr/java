package ru.gb.lesson4.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class RandomSelector {

    public static <T> List<T> generate(int size, Supplier<? extends T> tSupplier) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            T t = tSupplier.get();
            list.add(t);
        }
        return list;
    }

    public static <T> T select(List<T> list) {
        int randomIndex = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(randomIndex);
    }

}
