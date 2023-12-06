package org.example.t3;

import java.util.function.IntSupplier;

public class Main {
    public static void main(String[] args) {
        IntSupplier i1 = () -> 5;
        System.out.println(i1.getAsInt());
    }
}
