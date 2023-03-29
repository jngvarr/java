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
=======
//public class Solution {
//    public static void main(String[] args) throws InterruptedException {
//        for (int i = 30; i >= 0; i--) {
//            System.out.println(i);
>>>>>>> 3e1b8714933216908935f2cd9878e75365861c7b
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

