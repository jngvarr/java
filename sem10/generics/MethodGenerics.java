package ru.gb.lesson4.generics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MethodGenerics {

    public static void main(String[] args) {
        // A <- B <- C <- D <- E
        List<Alpha> alphaList = new ArrayList<>();
        List<Beta> betaList = new ArrayList<>();
        List<Gamma> gammaList = new ArrayList<>();
        List<Delta> deltaList = new ArrayList<>();
        List<Epsilon> epsilonList = new ArrayList<>();



        //PECS Producer Extends Consumer Super


        List<Hero> heroes = new ArrayList<>();
        heroes.add(new Hero());
        heroes.add(new AmazingHero());
        heroes.add(new AnotherHero());

        List<AmazingHero> amazingHeroes = new ArrayList<>();

        Comparator<Hero> heroComparator = (h1, h2) -> {
            throw new UnsupportedOperationException();
        };
        Comparator<AmazingHero> amazingHeroComparator = (h1, h2) -> {
            return Double.compare(h1.damage(), h2.damage());
        };
        Comparator<Object> objectComparator = (o1, o2) -> Integer.compare(o1.hashCode(), o2.hashCode());

        Collections.sort(heroes, heroComparator);
        Collections.sort(amazingHeroes, heroComparator);


        // Consumer Super


    }

    static class AnotherHero extends Hero {
        public void foo() {

        }
    }

    static class Hero {
        public double getHealthPoint() {
            return 1.0;
        }
    }

    static class AmazingHero extends Hero {
        public double damage() {
            return 1.0;
        }
    }

    static class Alpha {

    }

    static class Beta extends Alpha {

    }

    static class Gamma extends Beta {

    }

    static class Delta extends Gamma {

    }

    static class Epsilon extends Delta {

    }


}
