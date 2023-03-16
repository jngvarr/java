package ru.gb.lesson4.questions;

import java.util.Comparator;

public class HeroComparators {

    public static Comparator<Main.Hero> byDamage() {
        return Comparator.comparingInt(Main.Hero::getDamage);
    }

    public static Comparator<Main.Hero> byDamageReverse() {
        return byDamage().reversed();
    }

}
