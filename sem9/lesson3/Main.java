package ru.gb.lesson3;

import ru.gb.lesson2.Color;

public class Main {

    public static void main(String[] args) {
        DayOfWeek monday = DayOfWeek.MONDAY;
        System.out.println(monday);

        System.out.println(monday.name());
        System.out.println(monday.ordinal());

//        DayOfWeek mondayValueOf = DayOfWeek.valueOf("MONDAY1234");
//        System.out.println(mondayValueOf.ordinal());

        String text = "My Text value";
        System.out.println(text);
        System.out.println(Color.GREEN.paint(text));
        System.out.println(text);
    }

}
