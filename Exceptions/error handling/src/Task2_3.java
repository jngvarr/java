import java.io.FileNotFoundException;

//    Задание 3
//    Если необходимо, исправьте код:
public class Task2_3 {
    public static void main(String[] args) { //throws Exception -  main не кидает исключения, все исключения обработаны
        try {
            int a = 90;
            int b = 3;
            System.out.println(a / b);
            printSum(23, 234);
            int[] abc = {1, 2};
            abc[3] = 9;
//        } catch (NullPointerException ex) {   В данном коде такое исключение возникнуть не может
//            System.out.println("Указатель не может указывать на null!");
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Массив выходит за пределы своего размера!");
        } catch (Throwable ex) { // данное исключение перемещаем в конец, т.к. оно экранирует остальные
            System.out.println("Что-то пошло не так...");
        }
    }

    public static void printSum(Integer a, Integer b) { //throws FileNotFoundException данное исключение здесь возникнуть не может
        System.out.println(a + b);
    }
}
