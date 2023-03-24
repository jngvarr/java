package ru.gb.lesson6.l;

public class LiskovDemo {

    public static void main(String[] args) {
        // SuperHero -> Player
        // MyCustomPlayer -> Player

    }

    static void foo(Player player) {
        if (player instanceof SuperHero hero) {
            hero.damage = 100;
        }

        // standard logic
    }

    static class Player {

    }

    static class SuperHero extends Player {

        public int damage;

    }

    static class MyCustomerPlayer extends Player {

    }

}
