import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
// 1. Создать метод, который проверяет, является ли строка палиндромом.
// Палиндром - шалаш
        System.out.println("Введите слово для проверки на палиндром: ");
        Scanner scan = new Scanner(System.in);
        String str = scan.nextLine();
        System.out.printf("Введенное слово %s%n", isPalindrome(str) ? "палиндром" : "не палиндром");
    }
    public static boolean isPalindrome(String str) {
        boolean result = false;
        for (int i = 0; i < str.length() / 2; i++) {
            result = str.charAt(i) == str.charAt(str.length() - 1 - i);
        }
        return result;
    }
}