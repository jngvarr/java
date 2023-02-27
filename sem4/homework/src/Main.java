package com.company;
//Реализовать консольное приложение, которое:
//        Принимает от пользователя строку вида
//        text~num
//        Нужно рассплитить строку по ~, сохранить text в связный список на позицию num.
//        Если введено print~num, выводит строку из позиции num в связном списке и удаляет её из списка.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean input = true;
        ArrayList<String> ar = new ArrayList<String>();
        while (input) {
            System.out.println("For exit type: \"quit~\"");
            String[] inputString = sc.nextLine().split("~");
            if (inputString[0].equalsIgnoreCase("quit")) {
                input = false;
            } else if (inputString[0].equalsIgnoreCase("print")) {
                System.out.printf("Removing element: %s%n",ar.get(Integer.parseInt(inputString[1])));
                ar.remove(Integer.parseInt(inputString[1]));
                ar.add(Integer.parseInt(inputString[1]), "");
            } else if (ar.size() < Integer.parseInt(inputString[1])) {
                for (int i = ar.size(); i < Integer.parseInt(inputString[1]); i++) {
                    ar.add("");
                }
                ar.add(Integer.parseInt(inputString[1]), inputString[0]);
            } else if (ar.size() >= Integer.parseInt(inputString[1])) {
                ar.remove(Integer.parseInt(inputString[1]));
                ar.add(Integer.parseInt(inputString[1]), inputString[0]);
            }
            System.out.printf("Current List: %s%n", ar.toString());
        }
    }
}