import java.util.Arrays;

// Реализуйте метод, принимающий в качестве аргументов два целочисленных массива, и возвращающий новый массив,
// каждый элемент которого равен разности элементов двух входящих массивов в той же ячейке.
// Если длины массивов не равны, необходимо как-то оповестить пользователя.
public class Task1_2 {
    public static void main(String[] args) {
        int[] arr1 = {4, 7, 9};
        int[] arr2 = {5, 8, 2, 9};
        System.out.println(Arrays.toString(arraysDifference(arr1, arr2)));
    }

    public static int[] arraysDifference(int[] arr1, int[] arr2) {
        int[] resultArr = new int[Math.max(arr1.length, arr2.length)];
        try {
            for (int i = 0; i < resultArr.length; i++) {
                resultArr[i] = arr1[i] - arr2[i];
            }
            return resultArr;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("При исполнении кода возникло исключение " + e.getClass().getSimpleName() + ":" + e.getMessage());
            return null;
        }
    }
}
