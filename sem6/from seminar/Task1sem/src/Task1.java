import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/*Напишите метод, который заполнит массив из 1000 элементов случайными числами от 1 до 24.
Создайте метод, в который передается созданный массив и с помощью SET вычисляется процент уникальных значений в массиве и
возвращает его в виде числа с плавающей запятой.
 */
public class Task1 {
    public static void main(String[] args) {
        int[] newArray = createArray(25, 25);
//        System.out.println(Arrays.toString(newArray));
        uniqueStat(newArray);
    }

    public static int[] createArray(int bounds, int elementsQuantity) {
        int[] array = new int[elementsQuantity];
        for (int i = 0; i < array.length; i++) {
            array[i] = ThreadLocalRandom.current().nextInt(bounds);
        }
        return array;
    }

    public static void uniqueStat(int[] array) {
        Set<Integer> unique = new HashSet<>();
        for (int elem : array) {
            if (!unique.contains(elem)) {
                unique.add(elem);
            }
        }
        System.out.println(Arrays.toString(array));
        System.out.println(unique);
        System.out.println(100.0 * unique.size() / array.length + "%");
    }

}