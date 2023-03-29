//Задача: Написать программу, которая вводит с клавиатуры 20 чисел и выводит их в убывающем порядке.
package com.javarush.task.task07.task0728;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/*
В убывающем порядке
*/

public class Solution {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int[] array = new int[10];
        for (int i = 0; i < array.length; i++) {
            array[i] = Integer.parseInt(reader.readLine());

        }

        sort(array);

        for (int x : array) {
            System.out.println(x);
        }
    }

    public static void sort(int[] array) {
        int temp;
        int minIndex = 0;
        int min = array[minIndex];
        for (int i = 0; i < array.length-2; i++) {
            for (int j = 1; j < array.length-i - 1; j++) {
                if (min > array[j]) {
                    min = array[j];
                    minIndex = j;
                }
            }
            temp = array[array.length - i - 1];
            array[array.length - i - 1] = min;
            array[minIndex] = temp;
            minIndex = 0;
            min = array[minIndex];//1294738831
        }

    }
}
//Задача: Программа вводит строки, пока пользователь не введёт пустую строку (нажав enter).
// Потом она конвертирует строки в верхний регистр (Мама превращается в МАМА) и выводит их на экран.
//Новая задача: Программа вводит строки, пока пользователь не введёт пустую строку (нажав enter).
//Потом программа строит новый список. Если в строке чётное число букв, строка удваивается, если нечётное - утраивается.
//Программа выводит содержимое нового списка на экран.

//package com.javarush.task.task07.task0727;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///*
//Меняем функциональность
//*/
//
//public class Solution {
//    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//
//        ArrayList<String> strings = new ArrayList<String>();
//        while (true) {
//            String string = reader.readLine();
//            if (string == null || string.isEmpty()) break;
//            strings.add(string);
//        }
//
//        ArrayList<String> resultStrings = new ArrayList<String>();
//        for (int i = 0; i < strings.size(); i++) {
//            String string = strings.get(i);
//            if (string.length() % 2 == 0) {
//                resultStrings.add(string.toUpperCase() + " " + string.toUpperCase());
//            } else {
//                resultStrings.add(string.toUpperCase() + " " + string.toUpperCase() + " " + string.toUpperCase());
//            }
//        }
//
//        for (int i = 0; i < resultStrings.size(); i++) {
//            System.out.print(resultStrings.get(i) + " ");
//        }
//    }
//}


//Задача: Программа вводит с клавиатуры данные про котов и выводит их на экран.
//Требования:
//        •	Программа должна считывать данные с клавиатуры.
//        •	Если пользователь ввел: Barsik, 6, 5 и 22 (каждое значение с новой строки), то программа должна вывести "Cat's name: Barsik, age: 6, weight: 5, tail: 22".
//        •	Если пользователь ввел: Murka, 8, 7 и 20 (каждое значение с новой строки), то программа должна вывести "Cat's name: Murka, age: 8, weight: 7, tail: 20".
//        •	Если пользователь ввел: Barsik, 6, 5, 22, Murka, 8, 7 и 20 (каждое значение с новой строки), то программа должна вывести две строки: "Cat's name: Barsik, age: 6, weight: 5, tail: 22" и "Cat's name: Murka, age: 8, weight: 7, tail: 20".
//        •	Если пользователь ввел пустую строку вместо имени, то программа должна вывести данные на экран и завершиться.

//package com.javarush.task.task07.task0726;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///*
//Не компилируется задача про котиков
//*/
//
//public class Solution {
//    public final static ArrayList<Cat> CATS = new ArrayList<>();
//
//    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//
//        while (true) {
//            String name = reader.readLine();
//            if (name == null || name.isEmpty()) {
//                break;
//            }
//            int age = Integer.parseInt(reader.readLine());
//            int weight = Integer.parseInt(reader.readLine());
//            int tailLength = Integer.parseInt(reader.readLine());
//
//
//            Cat cat = new Cat(name, age, weight, tailLength);
//            CATS.add(cat);
//        }
//
//        printList();
//    }
//
//    public static void printList() {
//        for (int i = 0; i < CATS.size(); i++) {
//            System.out.println(CATS.get(i));
//        }
//    }
//
//    public static class Cat {
//        private String name;
//        private int age;
//        private int weight;
//        private int tailLength;
//
//        Cat(String name, int age, int weight, int tailLength) {
//            this.name = name;
//            this.age = age;
//            this.weight = weight;
//            this.tailLength = tailLength;
//        }
//
//        @Override
//        public String toString() {
//            return "Cat's name: " + name + ", age: " + age + ", weight: " + weight + ", tail: " + tailLength;
//        }
//    }
//}
//Переставь один модификатор static, чтобы пример скомпилировался.
//package com.javarush.task.task07.task0725;

/*
Переставь один модификатор static
*/

//public class Solution {
//    public final static int A = 5;
//    public final static int B = 2;
//    public final static int C = A * B;
//
//    public static void main(String[] args) {
//    }
//
//    public  int getValue() {
//        return C;
//    }
//}
//package com.javarush.task.task07.task0718;
//Создай класс Human с полями имя(String), пол(boolean), возраст(int), отец(Human), мать(Human). Создай объекты и
//заполни их так, чтобы получилось: Два дедушки, две бабушки, отец, мать, трое детей. Вывести объекты на экран.
//Написать программу, которая ведёт обратный отсчёт с 30 до 0, а в конце выводит на экран текст "Бум!".
//Программа должна уменьшать число 10 раз в секунду. Для того чтобы вставить в программу задержку, воспользуйся функцией:
//Thread.sleep(100); //задержка на одну десятую секунды.
//package com.javarush.task.task07.task0724;

//import java.util.ArrayList;

/*
Семейная перепись
*/
//public class Solution {
//    public static void main(String[] args) {
//        Human vasja = new Human("Вася", true, 65);
//        Human fjokla = new Human("Фёкла", false, 60);
//        Human kolja = new Human("Коля", true, 70);
//        Human natasha = new Human("Наташа", false, 60);
//        Human galja = new Human("Галина", false, 32, vasja, fjokla);
//        Human dimon = new Human("Дмитрий", true, 37, kolja, natasha);
//        Human petja = new Human("Петя", false, 13, dimon ,galja);
//        Human sonja = new Human("Петя", false, 10, dimon ,galja);
//        Human polja= new Human("Петя", false, 7, dimon ,galja);
//
//        System.out.println(vasja);
//        System.out.println(fjokla);
//        System.out.println(kolja);
//        System.out.println(natasha);
//        System.out.println(galja);
//        System.out.println(dimon);
//        System.out.println(petja);
//        System.out.println(sonja);
//        System.out.println(polja);
//    }
//
//    public static class Human {
//        String name;
//        boolean sex;
//        int age;
//        Human father;
//        Human mother;
//
//        public Human(String name, boolean sex, int age) {
//            this.name = name;
//            this.sex = sex;
//            this.age = age;
//        }
//
//        public Human(String name, boolean sex, int age, Human father, Human mother) {
//            this.name = name;
//            this.sex = sex;
//            this.age = age;
//            this.father = father;
//            this.mother = mother;
//        }
//
//
//        public String toString() {
//            String text = "";
//            text += "Имя: " + this.name;
//            text += ", пол: " + (this.sex ? "мужской" : "женский");
//            text += ", возраст: " + this.age;
//
//            if (this.father != null) {
//                text += ", отец: " + this.father.name;
//            }
//
//            if (this.mother != null) {
//                text += ", мать: " + this.mother.name;
//            }
//
//            return text;
//        }
//    }
//}
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///*
//Проверка на упорядоченность
//*/
//
//public class Solution {
//    public static void main(String[] args) throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        ArrayList<String> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            list.add(reader.readLine());
//        }
//        for (int i = 0; i < list.size() - 1; i++) {
//            if (list.get(i).length() > list.get(i + 1).length()) {
//                System.out.println(i + 1);
//                break;
//
//Написать программу, которая ведёт обратный отсчёт с 30 до 0, а в конце выводит на экран текст "Бум!".
//Программа должна уменьшать число 10 раз в секунду. Для того чтобы вставить в программу задержку, воспользуйся функцией:
//Thread.sleep(100); //задержка на одну десятую секунды.
//package com.javarush.task.task07.task0723;

/*
Обратный отсчёт Ӏ Java Syntax: 7 уровень, 12 лекция
*/

//package com.javarush.task.task07.task0717;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///*
//Удваиваем слова
//*/
//
//public class Solution {
//    public static void main(String[] args) throws Exception {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        ArrayList<String> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            list.add(reader.readLine());
//        }
//        ArrayList<String> result = doubleValues(list);
//
//        for (String s : result) {
//            System.out.println(s);
//        }
//    }
//
//    public static ArrayList<String> doubleValues(ArrayList<String> list) {
//        ArrayList<String> tempList = new ArrayList<>();
//        for (String l : list) {
//            tempList.add(l);
//            tempList.add(l);
//        }
//        return tempList;
//    }
//}
//package com.javarush.task.task07.task0716;
//
//import java.util.ArrayList;
//
///*
//Р или Л
//*/
//
//public class Solution {
//    public static void main(String[] args) {
//        ArrayList<String> strings = new ArrayList<>();
//        strings.add("роза");
//        strings.add("лоза");
//        strings.add("лира");
//        strings = fix(strings);
//
//        for (String string : strings) {
//            System.out.println(string);
//        }
//    }
//
//    public static ArrayList<String> fix(ArrayList<String> strings) {
//
//        String r = "р";
//        String l = "л";
//
//        ArrayList<String> result = new ArrayList<>();
//        for (String string : strings) {
//            boolean isR = string.contains(r);
//            boolean isL = string.contains(l);
//
//            if (isR && !isL) {
//                continue;
//            }
//
//            if (!isR && isL) {
//                result.add(string);
//            }
//
//            result.add(string);
//        }
//        return result;
//    }
//}package com.javarush.task.task07.task0709;
//
//import java.util.ArrayList;
//
///*
//Р или Л
//*/
//
//public class Solution {
//    public static void main(String[] args) {
//        ArrayList<String> strings = new ArrayList<String>();
//        strings.add("роза");
//        strings.add("лоза");
//        strings.add("упор");
//        strings.add("вода");
//        strings.add("мера");
//        strings.add("лира");
//        strings = fix(strings);
//
//        for (String string : strings) {
//            System.out.println(string);
//        }
//    }
//
//    public static ArrayList<String> fix(ArrayList<String> strings) {
//        boolean toRemove = false;
//        for (int i = strings.size() - 1; i >= 0; i--) {
//            toRemove=false;
//            if (strings.get(i).contains("р") && !strings.get(i).contains("л")) {
//                toRemove = true; //strings.remove(i);
//            }
//            if (strings.get(i).contains("л") && !strings.get(i).contains("р")) {
//                strings.add(strings.get(i));
//            }
//            if (toRemove) strings.remove(i);
//        }
//        return strings;
//    }
//}

//}  public static ArrayList<String> fix(ArrayList<String> strings) {
//        boolean toRemove = false;
//        for (int i = strings.size()-1; i >= 0; i--) {
//            if (strings.get(i).contains("р") && strings.get(i).contains("л")) {
//
//            } else {
//                if (strings.get(i).contains("р")) {
//                    toRemove=true;
//                }
//                if (strings.get(i).contains("л")) {
//                    strings.add( strings.get(i));
//                }
//                if (toRemove) strings.remove(i);
//            }
//        }
//        return strings;
//    }
//
//}
//package com.javarush.task.task07.task0709;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///*
//Выражаемся покороче
//*/
//
//public class Solution {
//    public static void main(String[] args) throws Exception {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        ArrayList<String> str = new ArrayList<>();
//        str.add(reader.readLine());
//        int shortestString = str.get(0).length();
//        for (int i = 1; i < 5; i++) {
//            str.add(reader.readLine());
//            if (str.get(i).length() < shortestString) {
//                shortestString = str.get(i).length();
//            }
//        }
//        for (String s : str) {
//            if (s.length() == shortestString) {
//                System.out.println(s);
//            }
//        }
//    }
//}


////package com.javarush.task.task07.task0715;
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

