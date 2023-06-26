// Реализуйте метод, принимающий в качестве аргументов два целочисленных массива, и возвращающий новый массив,
// каждый элемент которого равен частному элементов двух входящих массивов в той же ячейке. Если длины массивов не равны,
// необходимо как-то оповестить пользователя.
// Важно: При выполнении метода единственное исключение, которое пользователь может увидеть - RuntimeException, т.е. ваше.

import java.util.Arrays;

public class Task3 {
    public static void main(String[] args) {
        int[] arr1 = {4, 7, 9};
        int[] arr2 = {2, 3, 2, 6};
        System.out.println(Arrays.toString(arraysDifference(arr1, arr2)));
    }

    public static int[] arraysDifference(int[] arr1, int[] arr2) {
        int[] resultArr = new int[Math.max(arr1.length, arr2.length)];
        if (arr1.length != arr2.length) {
            throw new RuntimeException("Заданные массивы разной длины!");
        } else {
            for (int i = 0; i < resultArr.length; i++) {
                resultArr[i] = arr1[i] / arr2[i];
            }
        }
        return resultArr;
    }
}