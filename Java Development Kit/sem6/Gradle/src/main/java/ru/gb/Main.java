package ru.gb;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.*;

public class Main {
    public static void main(String[] args) {
//        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
//        System.out.println(Lists.reverse(list));
//        String string = new String("Hello World 1");
//        System.out.println(Lists.charactersOf(string));
//        System.out.println(Lists.partition(Lists.charactersOf(string),2));
//
//        Set<Integer> set = new HashSet<>(list);
//        System.out.println(set);
//        System.out.println(Sets.union(new HashSet<>(list), new HashSet<>(Set.of(string))));// объединение двух сетов
//        System.out.println(Sets.union(new HashSet<>(Set.of(list)), new HashSet<>(Set.of(Lists.charactersOf(string),2)))); // объединение двух сетов
//        System.out.println(Sets.intersection(set, new HashSet<>(Set.of(Lists.partition(Lists.charactersOf(string),2))))); // выделение общего элемента из двух сетов
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        Set<String> set3 = new HashSet<>();
        set1.add("1");
        set1.add("2");
        set1.add("3");
        set2.add("1");
        set2.add("2");
        set2.add("c");
        System.out.println(Sets.intersection(set1, set2)); // выделение общего элемента из двух сетов
        System.out.println(Sets.symmetricDifference(set1, set2)); // выделение отличных из двух сетов
        System.out.println(Sets.symmetricDifference(set1, set2)); // выделение отличных из двух сетов
        set3 = Sets.union(Sets.symmetricDifference(set1, set2));
    }
}