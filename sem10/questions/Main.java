package ru.gb.lesson4.questions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class Main {

    public static void main(String[] args) {
//        List<Hero> heroes = new ArrayList<>(List.of(
//                new Hero(),
//                new Hero(),
//                new Hero(),
//                new Hero(),
//                new Hero()
//        ));
//
//        Collections.sort(heroes, HeroComparators.byDamage());
//        Collections.sort(heroes, HeroComparators.byDamageReverse());

        System.out.println(DayOfWeek.MONDAY);
        System.out.println(DayOfWeek.TUESDAY);

        List<Integer> integers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7));
        Integer random = RandomSelector.select(integers);//

        Supplier<Integer> integerSupplier = () -> ThreadLocalRandom.current().nextInt(10);
//        List<Long> numbers = RandomSelector.generate(10, integerSupplier);

    }

    static class Hero {

        private int damage;

        public Hero() {
            this.damage = ThreadLocalRandom.current().nextInt(10);
        }

        public int getDamage() {
            return damage;
        }
    }

}
