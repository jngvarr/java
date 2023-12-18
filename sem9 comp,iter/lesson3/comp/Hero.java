package ru.gb.lesson3.comp;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Hero implements Comparable<Hero> {

    public static void main(String[] args) {
        List<Hero> heroes = IntStream.range(0, 10)
                .mapToObj(it -> ThreadLocalRandom.current().nextInt(100))
//                .map(Double::valueOf)
                .map(Double::valueOf)
//                .map(Hero::new)
//                .map(d -> new Hero(d))
                .map(Hero::new)
                .sorted()
                .toList();
        System.out.println(heroes);

        Hero hero1 = new Hero(1.5);
        Hero hero2 = new Hero(3.5);

        int i = hero1.compareTo(hero2);


    }

    private double damage;
    private int hp;

    public Hero(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    @Override
    public int compareTo(Hero o) {
        if (this.damage > o.damage) {
            return 1;
        } else if (damage == o.damage) {
            if (hp == o.hp) {
                return 0;
            } else if (hp > o.hp) {
                return 1;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return String.valueOf(damage);
    }
}
