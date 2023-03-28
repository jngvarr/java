package com.javarush.task.task07.task0709;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
Выражаемся покороче
*/

public class Solution {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<String> str = new ArrayList<>();
        str.add(reader.readLine());
        int shortestString = str.get(0).length();
        for (int i = 1; i < 5; i++) {
            str.add(reader.readLine());
            if (str.get(i).length() < shortestString) {
                shortestString = str.get(i).length();
            }
        }
        for (String s : str) {
            if (s.length() == shortestString) {
                System.out.println(s);
            }
        }
    }
}


////package com.javarush.task.task07.task0715;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///*
//Продолжаем мыть раму
//*/
//
//public class Solution {
//    public static void main(String[] args) {
//        ArrayList<String> list = new ArrayList<>();
//        list.addAll(Arrays.asList("мама", "мыла", "раму"));
//        for (int i = 0; i < list.size(); i++) {
//            list.add((i + 1), "именно");
//            i++;
//        }
//        for (String s : list) {
//            System.out.println(s);
//        }
//    }
//}
//

////package com.javarush.task.task07.task0714;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///*
//Слова в обратном порядке
//*/
//
//public class Solution {
//    public static void main(String[] args) throws Exception {
//        ArrayList<String> str = new ArrayList<>();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        for (int i = 0; i < 5; i++) {
//            str.add(reader.readLine());
//        }
//        str.remove(2);
//        for (int i = str.size()-1; i >= 0; i--) {
//            System.out.println(str.get(i));
//        }
//    }
//}
//

////package com.javarush.task.task07.task0713;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.Arrays;
//
///*
//Играем в Золушку
//*/
//
//public class Solution {
//    public static void main(String[] args) throws Exception {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        ArrayList<Integer> nums = new ArrayList<>();
//        ArrayList<Integer> dividedByThree = new ArrayList<>();
//        ArrayList<Integer> evenNums = new ArrayList<>();
//        ArrayList<Integer> otherNums = new ArrayList<>();
//
//        for (int i = 0; i < 20; i++) {
//            nums.add(Integer.parseInt(reader.readLine()));
//        }
//        for (int n : nums) {
//            if (n % 3 == 0) {
//                dividedByThree.add(n);
//            }
//            if (n % 2 == 0) {
//                evenNums.add(n);
//            }
//            if (n % 2 != 0 && n % 3 != 0) otherNums.add(n);
//
//        }
//        printList(dividedByThree);
//        printList(evenNums);
//        printList(otherNums);
//    }
//
//    public static void printList(ArrayList<Integer> list) {
//        for (int n:list             ) {
//            System.out.println(n);
//        }
//
//    }
//}


//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//
//public class Solution {
//    public static void main(String[] args) throws IOException {
//        ArrayList<Integer> list1 = new ArrayList<Integer>();   //создание списка
//        Collections.addAll(list1, 1, 5, 6, 11, 3, 15, 7, 8);   //заполнение списка
//
//        ArrayList<Integer> list2 = new ArrayList<Integer>();
//        Collections.addAll(list2, 1, 8, 6, 21, 53, 5, 67, 18);
//
//        ArrayList<Integer> result = new ArrayList<Integer>();
//
//        result.addAll(list1);   //добавление всех значений из одного списка в другой
//        result.addAll(list2);
//
//        for (Integer x : result)   //быстрый for по всем элементам, только для коллекций
//        {
//            System.out.print(x+ " ");
//        }
//    }
//}
//package com.javarush.task.task07.task0712;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;

/*
Самые-самые
*/

//    public class Solution {
//        public static void main(String[] args) throws IOException {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            ArrayList<String> strings = new ArrayList<>();
//            strings.add(reader.readLine());
//            int longestString = strings.get(0).length();
//            int shortestString = strings.get(0).length();
//            for (int i = 1; i < 10; i++) {
//                strings.add(reader.readLine());
//                if (strings.get(i).length() > longestString) {
//                    longestString = strings.get(i).length();
//                }
//                if (strings.get(i).length() < shortestString) {
//                    shortestString = strings.get(i).length();
//                }
//            }
//            for (String s : strings) {
//                if (s.length() == longestString || s.length() == shortestString) {
//                    System.out.println(s);
//                    break;
//                }
//            }
//        }
//    }


//package com.javarush.task.task07.task0711;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///*
//Удалить и вставить
//*/
//
//public class Solution {
//    public static void main(String[] args) throws Exception {
//        ArrayList<String> strings = new ArrayList<>();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        for (int i = 0; i < 5; i++) {
//            strings.add(reader.readLine());
//        }
//        for (int i = 0; i < 13; i++) {
//            strings.add(0, strings.remove(strings.size() - 1));
//        }
//        for (String s : strings) {
//            System.out.println(s);
//        }
//    }
//}


//package com.javarush.task.task07.task0710;

//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//
///*
//В начало списка
//*/
//
//public class Solution {
//    public static void main(String[] args) throws Exception {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        ArrayList<String> strings = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            strings.add(0, reader.readLine());
//        }
//        for (String s : strings){
//            System.out.println(s);
//        }
//    }
//}
//
//
//


//import java.io.BufferedReader;
//        import java.io.InputStreamReader;
//        import java.util.ArrayList;

/*
Самая длинная строка javarush.task.task07.task0708
*/
//
//public class Solution {
//    private static ArrayList<String> strings;
//
//    public static void main(String[] args) throws Exception {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        strings = new ArrayList<>();
//        int maxLengthString = 0;
//        for (int i = 0; i < 5; i++) {
//            strings.add(reader.readLine());
//            if (strings.get(i).length() > maxLengthString) {
//                maxLengthString = strings.get(i).length();
//            }
//        }
//        for (String s : strings) {
//            if (s.length() == maxLengthString) {
//                System.out.println(s);
//            }
//        }
//    }
//}
