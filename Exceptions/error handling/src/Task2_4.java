import java.io.Console;
import java.util.Scanner;

//Разработайте программу, которая выбросит Exception, когда пользователь вводит пустую строку.
//Пользователю должно показаться сообщение, что пустые строки вводить нельзя.
public class Task2_4 {
    public static void main(String[] args) {
        inputSomething();
    }

    public static void inputSomething() {
        //Console cons = System.console();
        Scanner sc = new Scanner(System.in);
        System.out.print("Введите что-либо: > ");
        if (sc.nextLine().equals("")) {
            throw new EmptyStringException("Ввод пустой строки недопустим!");
        }
 //System.arraycopy(element, 0, temp, 0, element.length);
    }
}

class EmptyStringException extends RuntimeException {
    public EmptyStringException(String message) {
        super(message);
    }
}
