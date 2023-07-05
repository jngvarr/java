import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        writeToFile(invitation());
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

    public static boolean isEnoughOrTooMuchData(String[] arr) {
        return arr.length == 6;
    }

    public static boolean isValidData(String[] arr) {
        for (int i = 0; i < 2; i++) {
            if (hasDigit(arr[i]) || !Character.isUpperCase(arr[i].charAt(0)))
                throw new FIOException("Неправильный формат ФИО!");
        }

        for (String s : arr) {
            if (s.isEmpty()) throw new EmptyDataException("Введена пустая строка");
        }
        if (!arr[3].matches("\\d{2}.\\d{2}.\\d{4}")) throw new WrongDateFormatException("Дата введена неверно!");

        if (arr[4].length() != 7 || !isDigit(arr[4]))
            throw new WrongPhoneFormatException("Неверный формат номера телефона");
        if (!isFMChar(arr[5])) throw new WrongSexFormatException("Пол указан неверно!");
        return true;
    }

    public static boolean isDigit(String str) {
        try {
            Double.parseDouble(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean hasDigit(String str) {
        for (char ch : str.toCharArray()) {
            if (Character.isDigit(ch)) return true;
        }
        return false;
    }

    public static boolean isFMChar(String str) {
        if (str.charAt(0) == 'm' || str.charAt(0) == 'f') return true;
        else return false;
    }

    public static void writeToFile(String[] arr) {

        if (!isEnoughOrTooMuchData(arr)) throw new NotEnoughOrTooMuchDataException(arr.length);
        else if (isValidData(arr)) {
            try (FileWriter writer = new FileWriter(arr[0] + ".txt", true)) {
                writer.write((new Human(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5].charAt(0))).toString());
                writer.flush();
            } catch (IOException e) {
                System.out.println("При записи в файл возникло исключениеЖ " + e.getClass().getSimpleName() + "" + e.getMessage());
            }
        }
    }
}