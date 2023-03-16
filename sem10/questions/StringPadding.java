package ru.gb.lesson4.questions;

public class StringPadding {

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    public static void main(String args[]) throws Exception {
        System.out.println(padRight("112341234dfasdfajkhflkashfkjasfashflkajshdf123", 20) + "*");
        System.out.println(padLeft("2", 20) + "*");
    }

}
