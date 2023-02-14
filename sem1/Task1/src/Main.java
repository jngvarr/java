import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

    public class Main {
        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
// Повторить вывод строки заданное количество раз
            System.out.println("Задание №3.");
            System.out.print("Введите строку: ");
            String str = sc.nextLine();
            System.out.print("Введите количество повторений: ");
            int rep = sc.nextInt();
            printString(str, rep);
            System.out.println();

// проверить, что сумма a и b лежит между 10 и 20
            System.out.println("Задание №1.");
            System.out.print("Введите число a: ");
            int a = sc.nextInt(); // следующее число в консоли
            System.out.print("Введите число b: ");
            int b = sc.nextInt(); // следующее число в консоли
            System.out.printf(" 10 < a + b < 20 - %s%n", isSumBetween10And20(a, b));
            System.out.println();

// Проверить, что х положительное
            System.out.println("Задание №2.");
            System.out.print("Введите число Х: ");
            int x = sc.nextInt();
            System.out.printf("Веденное число больше 0 - %s".formatted(isPositive(x))+"\n");
            System.out.println();

//Проверить, является ли год високосным, если да - return true
            System.out.println("Задание №4.");
            System.out.print("Введите год: ");
            int year = sc.nextInt(); // следующее число в консоли
            System.out.println(year + "й год високосный? - " + isLeapYear(year));
            System.out.println();

// Метод должен вернуть массив длины len, каждое значение qкоторого равно initialValue
            System.out.println("Задание №5.");
            System.out.print("Задайте длину массива: ");
            int len = sc.nextInt();
            System.out.print("Задайте значение элементов массива: ");
            int val = sc.nextInt();
            System.out.println(Arrays.toString(createArray(len, val)));
            System.out.println();

            System.out.println("Задание №6");
            String[] arr1 = {"aaa", "aab", "aa"};
            String[] arr2 = {"abc", "bca", "cda"};
            System.out.printf("Максимальный общий префикс строк %s%s%s%n", Arrays.toString(arr1), " - ", findCommonPrefix(arr1));
            System.out.printf("Максимальный общий префикс строк %s%s%s%n", Arrays.toString(arr2), " - ", findCommonPrefix(arr2));
            System.out.println();

// 7. Задать целочисленный массив, состоящий из элементов 0 и 1.
// Например: [ 1, 1, 0, 0, 1, 0, 1, 1, 0, 0 ]. С помощью цикла и условия заменить 0 на 1, 1 на 0;
            System.out.println("Задание №7");
            int[] array = new int[15];
            for (int i = 0; i < array.length; i++) {
                array[i] = new Random().nextInt(2);
            }
            System.out.printf("Исходный массив %s%n ", Arrays.toString(array));
            System.out.printf("Реверсивный массив %s%n ", Arrays.toString(change_elements(array)));

// 8. Задать массив [ 1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1 ] пройти по нему циклом, и числа меньшие 6 умножить на 2;
            System.out.println("Задание №8");
            int[] arr = {1, 5, 3, 2, 11, 4, 5, 2, 4, 8, 9, 1};
            System.out.printf("Исходный массив %s%n ", Arrays.toString(arr));
            System.out.printf("Домножение элемента на 2 %s%n ", Arrays.toString(double_elements(arr)));

// 9. Создать квадратный двумерный целочисленный массив (количество строк и столбцов одинаковое),
// и с помощью цикла(-ов) заполнить его диагональные элементы единицами
            System.out.println("Задание №9");
            System.out.println("Задайте размер массива: ");
            int length = sc.nextInt();
            int[][] ar = new int[length][length];
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    ar[i][j] = length - i - 1 == j ? 1 : ar[i][j];
                    ar[i][j] = i == j ? 1 : ar[i][j];
                    System.out.print(ar[i][j]);
                }
                System.out.println();
            }
            System.out.println();

//      10. Задать одномерный массив и найти в нем минимальный и максимальный элементы
            System.out.println("Задание №10");
            System.out.println("Задайте размер массива: ");
            int size = sc.nextInt();
            int[] array_ = new int[size];
            for (int i = 0; i < array_.length; i++) {
                array_[i] = new Random().nextInt(20);
            }
            System.out.println(Arrays.toString(array_));
            Arrays.sort(array_);
            System.out.println();
            System.out.println("Минимальный элемент массива = " + array_[0]);
            System.out.println("Максимальный элемент массива = " +  array_[array_.length-1]);
        }

        public static boolean isSumBetween10And20 ( int a, int b){
            // проверить, что сумма a и b лежит между 10 и 20
            return a + b > 10 && a + b < 20;
        }
        public static boolean isPositive ( int x){
            // проверить, что х положительное
            return x > 0;
        }

        public static void printString (String source,int repeat){
            for (int i = 0; i < repeat; i++) {
                System.out.println(source);
            }
        }

        public static boolean isLeapYear ( int year){
            // Проверить, является ли год високосным, если да - return true
            return year % 400 == 0 || year % 4 == 0 & year % 100 != 0;
        }

        public static int[] createArray ( int len, int initialValue){
            // должен вернуть массив длины len, каждое значение которого равно initialValue
            int[] array = new int[len];
            Arrays.fill(array, initialValue);
            return array;
        }

        public static String findCommonPrefix (String[]source){
        /* Найти общий префикс среди слов из одного массива.
       ["aaa", "aab", "aa"] -> "aa", ["abc", "bca", "cda"] -> ""*/
            Arrays.sort(source);
            int min_str_length = Math.min(source[0].length(), source[source.length - 1].length());
            String first_str = source[0];
            String commonPrefix = null;
            for (int i = 1; i < source.length; i++) {
                String current = source[i];
                int char_num = 0;
                int lastCommon = 0;
                while (char_num < min_str_length) {
                    if (first_str.charAt(char_num) == current.charAt(char_num)) {
                        lastCommon++;
                        char_num++;
                    } else {
                        break;
                    }
                }
                commonPrefix = first_str.substring(0, lastCommon);
            }
            return commonPrefix;
        }

        public static int[] change_elements ( int[] arr){
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i] == 1 ? 0 : 1;
            }
            return arr;
        }


        public static int[] double_elements ( int[] arr){
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i] < 6 ? arr[i] * 2 : arr[i];
            }
            return arr;
        }

    }
