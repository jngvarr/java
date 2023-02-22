import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class homework {
    public static void main(String[] args) {

//1. Пусть дан произвольный список целых чисел, удалить из него четные числа (в языке уже есть что-то готовое для этого)
//2. Задан целочисленный список ArrayList. Найти минимальное, максимальное и среднее арифметическое из этого списка.
        Random rnd = new Random();
        ArrayList<Integer> randomList = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            randomList.add(rnd.nextInt(100));
        }
        oddRemain(randomList);
        minMaxAvg(randomList);
    }

    public static void oddRemain(ArrayList<Integer> randomList) {
        System.out.printf("Исходный список:  %s\n", randomList);
        randomList.removeIf(elem -> (elem % 2 == 0));
        System.out.printf("Список без четных элементов: %s\n", randomList);
    }

    public static void minMaxAvg(ArrayList<Integer> randList) {
        Collections.sort(randList);
        int summ = 0;
        double average = 0;
        for (int i = 0; i < randList.size(); i++) {
            summ  += randList.get(i);
        }
        average = summ / randList.size();

        System.out.printf("Минимальное значение списка: %s\n", randList.get(0));
        System.out.printf("Максимальное значение списка: %s\n", randList.get(randList.size() - 1));
        System.out.printf("Среднее значение списка: %s", average);
    }


}
