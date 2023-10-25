package ru.gb.task2;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String[] strArr1 = {"a", "f"};
        String[] strArr2 = {"a", "f"};
        System.out.println(compareArrays(strArr1, strArr2));

        Integer[] intArr1 = {1, 2};
        Integer[] intArr2 = {1, 2};

        System.out.println(compareArrays(intArr1, intArr2));
        System.out.println(compareArrays(intArr1, strArr2));
    }

    public static <T> boolean compareArrays(T[] arr1, T[] arr2) {
        if (arr1.length != arr2.length) return false;
        for (int i = 0; i < arr1.length; i++) {
            if (!arr1[i].equals(arr2[i])) {
                return false;
            }
        }
        return true;
    }
}
