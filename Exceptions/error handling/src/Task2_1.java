import java.util.Scanner;

// Реализуйте метод, который запрашивает у пользователя ввод дробного числа (типа float), и возвращает введенное
// значение. Ввод текста вместо числа не должно приводить к падению приложения, вместо этого, необходимо повторно
// запросить у пользователя ввод данных.

public class Task2_1 {
    public static void main(String[] args) {
        System.out.printf("Введенное число %.2f соответствуют требованию ввода.", doubleReturn());
    }

    public static float doubleReturn() {
        System.out.print("Введите пожалуйста дробное число (типа float): > ");
        Scanner sc = new Scanner(System.in);
        float num = 0;
        if (sc.hasNextFloat()) {
            num = sc.nextFloat();
        } else {
            System.out.println("Введенное число не соответствуют требованию ввода.");
            doubleReturn();
        }
        return num;
    }
}
