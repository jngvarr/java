//Создай класс Human с полями имя(String), пол(boolean), возраст(int), отец(Human), мать(Human). Создай объекты и
//заполни их так, чтобы получилось: Два дедушки, две бабушки, отец, мать, трое детей. Вывести объекты на экран.
//Написать программу, которая ведёт обратный отсчёт с 30 до 0, а в конце выводит на экран текст "Бум!".
//Программа должна уменьшать число 10 раз в секунду. Для того чтобы вставить в программу задержку, воспользуйся функцией:
//Thread.sleep(100); //задержка на одну десятую секунды.
//package com.javarush.task.task07.task0724;

/*
Семейная перепись
*/

public class Solution {
    public static void main(String[] args) {
    }

    public static class Human {
        String name;
        boolean sex;
        int age;
        Human father;
        Human mother;

        public String toString() {
            String text = "";
            text += "Имя: " + this.name;
            text += ", пол: " + (this.sex ? "мужской" : "женский");
            text += ", возраст: " + this.age;

            if (this.father != null) {
                text += ", отец: " + this.father.name;
            }

            if (this.mother != null) {
                text += ", мать: " + this.mother.name;
            }

            return text;
        }
    }
}
//Написать программу, которая ведёт обратный отсчёт с 30 до 0, а в конце выводит на экран текст "Бум!".
//Программа должна уменьшать число 10 раз в секунду. Для того чтобы вставить в программу задержку, воспользуйся функцией:
//Thread.sleep(100); //задержка на одну десятую секунды.
//package com.javarush.task.task07.task0723;

/*
Обратный отсчёт Ӏ Java Syntax: 7 уровень, 12 лекция
*/

//public class Solution {
//    public static void main(String[] args) throws InterruptedException {
//        for (int i = 30; i >= 0; i--) {
//            System.out.println(i);
//
//            Thread.sleep(100);
//        }
//
//        System.out.println("Бум!");
//    }
//}


//Создать список строк.
//Ввести строки с клавиатуры и добавить их в список.
//Вводить с клавиатуры строки, пока пользователь не введет строку "end". Саму строку "end" не учитывать.
//Вывести строки на экран, каждую с новой строки.
//package com.javarush.task.task07.task0722;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
///*
//Это конец
//*/
//
//public class Solution {
//    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        ArrayList<String> list = new ArrayList<>();
//        boolean run = true;
//        while (run) {
//            String string = reader.readLine();
//            if (string.equalsIgnoreCase("end")) {
//                run = false;
//            }else {
//                list.add(string);
//            }
//        }
//                for (String s : list) {
//                    System.out.println(s);
//                }
//    }
//}
//Создать массив на 20 чисел.
//Заполнить его числами с клавиатуры.
//Найти максимальное и минимальное числа в массиве.
//Вывести на экран максимальное и минимальное числа через пробел.

//package com.javarush.task.task07.task0721;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
///*
//Минимаксы в массивах
//*/
//
//public class Solution {
//    public static void main(String[] args) throws IOException {
//        int[]array = getInts();
//        int maximum = array[0];
//        int minimum = array[0];
//        for (int i = 1; i < array.length; i++) {
//            maximum = Math.max(maximum, array[i]);
//            minimum = Math.min(minimum, array[i]);
//        }
//        System.out.print(maximum + " " + minimum);
//    }
//    public static int[] getInts( ) throws IOException {
//        int[] arr = new int[20];
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        for (int i = 0; i < arr.length; i++) {
//            arr[i] = Integer.parseInt(reader.readLine());
//        }
//        return arr;
//    }
//}

//Ввести с клавиатуры 2 числа N и M.
//Ввести N строк и заполнить ими список.
//Переставить M первых строк в конец списка.
//Вывести список на экран, каждое значение с новой строки.
//package com.javarush.task.task07.task0721;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
//public class Solution {
//    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        int n = Integer.parseInt(reader.readLine());
//        int m = Integer.parseInt(reader.readLine());
//        ArrayList<String> list = new ArrayList<>();
//        for (int i = 0; i < n; i++) {
//            list.add(reader.readLine());
//        }
//        for (int i = 0; i < m; i++) {
//            list.add(list.remove(0));
//        }
//        for (String l : list) {
//            System.out.println(l);
//        }
//    }
//}

//Ввести с клавиатуры 10 чисел и заполнить ими список.
//Вывести их в обратном порядке. Каждый элемент - с новой строки.
//Использовать только цикл for.
//package com.javarush.task.task07.task0719;

//import java.io.BufferedReader;
////import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///*
//Вывести числа в обратном порядке
//*/
//
//public class Solution {
//    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        ArrayList<Integer> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            list.add(Integer.parseInt(reader.readLine()));
//        }
//        for (int i = list.size()-1; i >= 0; i--) {
//            System.out.println(list.get(i));
//        }
//    }
//}

