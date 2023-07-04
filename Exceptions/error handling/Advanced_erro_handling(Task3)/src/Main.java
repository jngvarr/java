import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        invitation();
    }

    public static String[] invitation() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Введите через пробел личные данные человека в следующем виде:\n\n" +
                "Фамилия Имя Отчество Дата_рождения Номер_телефона Пол\n\n" +
                "Необходимые форматы ввода:\n" +
                "Дата рождения - в формата dd.mm.yyyy;\n" +
                "Номер телефона - в виде семизначного числа;\n" +
                "Пол - символ латиницей f или m.\n" +
                "> ");
        return sc.nextLine().split(" ");
    }

    public static boolean isEnoughData(String[] arr) {
        if (arr.length != 6) return false;
        else return true;
    }

    public static boolean isValidData(String[] arr) {
        boolean validity = false;
        try {
            for (String s : arr) {
                if (s.length() == 0) return false;
            }
            if (arr[5].length() != 7 ) return false;
        } catch () {
        }
    }
}