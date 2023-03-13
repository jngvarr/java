package ru.gb.lesson3.iter;

import java.util.*;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) {
        List<Integer> integers = new ArrayList<>(List.of(1, 2, 1, 4, 5, 6, 7, 8));

        Iterator<Integer> iterator = integers.iterator();
        iterator.next(); // 1
        iterator.next(); // 2
        iterator.next(); // 1
        iterator.remove(); // remove 1
        iterator.remove();
        System.out.println(integers);

        integers.iterator().forEachRemaining(System.out::println);


        // [1, 2, 3, 4, 5, 6, 7, 8 ^]
//        for (Integer integer : integers) {
//            System.out.println(integer);
//        }
//
//        Iterator iterator = integers.iterator();
//
//        while(iterator.hasNext()) {
//            Integer integer = (Integer)iterator.next(); // 8
//            System.out.println(integer);
//        }

        int[] array = {8, 2, 5, 3, 9, 12, -3};
        IntArrayIterable intArrayIterable = new IntArrayIterable(array);
        for (int x: intArrayIterable) {
            System.out.print(x + " ");
        }


        Set<Integer> integerSet = new HashSet<>(); // Set extends Iterable
        integerSet.add(1);
        integerSet.add(2);
        integerSet.add(3);
        integerSet.add(4);
        integerSet.add(5);

        for (Integer integer : integerSet) {
            System.out.println(integer);
        }

    }

    static class IntArrayIterable implements Iterable<Integer> {

        private final int[] array;

        public IntArrayIterable(int[] array) {
            this.array = array;
        }

        @Override
        public Iterator<Integer> iterator() {
//            return new IntArrayIterator(array);
            return new Iterator<Integer>() {

                private int cursor = 0;

                @Override
                public boolean hasNext() {
                    return cursor < array.length;
                }

                @Override
                public Integer next() {
                    return array[cursor++];
                }
            };
        }
    }


    static class IntArrayIterator implements Iterator<Integer> {

        private final int[] array;
        private int cursor = 0;

        public IntArrayIterator(int[] array) {
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return cursor < array.length;
        }

        @Override
        public Integer next() {
            return array[cursor++];
        }
    }

}
