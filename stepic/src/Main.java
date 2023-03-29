import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //System.out.println("insert first number");
        double num1 = sc.nextDouble();
        //System.out.println("insert type of action");
        String action = sc.next();
        //System.out.println("insert second number");
        double num2 = sc.nextDouble();
        if (num2 != 0) {
            switch (action) {
                case "+":
                    System.out.println(num1 + num2);
                    break;
                case "-":
                    System.out.println(num1 - num2);
                    break;
                case "*":
                    System.out.println(num1 * num2);
                    break;
                case "/":
                    System.out.println(num1 / num2);
                    break;
                default:
                    System.out.println("ОШИБКА");
            }
        }
    }
}