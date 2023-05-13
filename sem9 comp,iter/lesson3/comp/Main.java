package ru.gb.lesson3.comp;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();

        Comparator<String> stringComparator = Comparator.comparingInt(String::length);
        Comparator<String> stringLengthComparator = (s1, s2) -> s1.length() - s2.length();

        Comparator<Integer> integerComparator = Integer::compareTo;
        // (int x, int y) -> int


        System.out.println(stringLengthComparator.compare("abcde", "ab"));
        System.out.println(stringLengthComparator.compare("abcde", "ddddd"));
        System.out.println(stringLengthComparator.compare("abcde", "aabddddddadsaag;akjfg"));

        List<String> strings = new ArrayList<>(List.of("abcde", "a", "SSS", "ddddd", "vcds", "adsfadfa"," fadsf ", "sdfsdfsfffff"));
        System.out.println(strings);
        Collections.sort(strings);

        "String".compareTo("anotherString");

        System.out.println(strings);

        // a < b, b < c => a < c транзитивность
        // a < b => b > a

        // Создать класс Notebook с полями:
        // 1. Стоимость (int)
        // 2. Оперативная память (int)
        // Нагенерить объектов этого класса, создать список и отсортировать его в трех вариантах:
        // 1. По возрастанию цены
        // 2. По убыванию цены
        // 3. По оперативке по убыванию. Если оперативки равны - по убыванию цены.
        // 4.+ придумать свои параметры и отсортировать по ним

        TreeSet<MyType> set = new TreeSet<>(new Comparator<MyType>() {
            @Override
            public int compare(MyType o1, MyType o2) {
                return 0;
            }
        });
        set.add(new MyType());
    }

    static class MyType implements Comparable<MyType> {

        @Override
        public int compareTo(MyType o) {
            return 0;
        }
    }

//    /**
//     * Компаратор, который сравнивает строки по длине: чем строка короче, тем она меньше.
//     */
    static class StringLengthComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            return s1.length() - s2.length();
        }

    }

}
