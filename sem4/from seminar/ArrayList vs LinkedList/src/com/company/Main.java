package com.company;

import java.util.ArrayList;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        int size = 10000000;
        ArrayList<Integer> al = new ArrayList<Integer>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            al.add(5);
        }
        long end = System.currentTimeMillis();
        System.out.println("Время выполнения участка ArrayList " + (end - start) + " мс");

        LinkedList<Integer> ls = new LinkedList<Integer>();
        start = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            al.add(5);
        }
        end = System.currentTimeMillis();
        System.out.println("Время выполнения участка LinkedList " + (end - start) + " мс");


    }
}
