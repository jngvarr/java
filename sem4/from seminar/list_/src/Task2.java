import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class Task2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Deque<String> dq = new ArrayDeque<String>();
        boolean input = true;
        while (input) {
            String string = sc.nextLine();
            if (string.equalsIgnoreCase("print")) {
                System.out.println(dq.toString());
            } else if (string.equalsIgnoreCase("revert")) {
                dq.pollFirst();
//                dq.removeFirst();
                System.out.println(dq.toString());
            } else if (string.equalsIgnoreCase("quit")) {
                input = false;
            } else dq.addFirst(string);
        }
//sc.close;
    }
}

