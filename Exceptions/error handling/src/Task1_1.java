// Реализуйте 3 метода, чтобы в каждом из них получить разные исключения

import java.util.Arrays;
public class Task1_1 {
    public static void main(String[] args) {
        // ArithmeticException
        div(9, 6);
        // NullPointerException
        division(null, 6);
        //ArrayIndexOutOfBoundsException
        int[] arr1 = {4, 7, 9};
        int[] arr2 = {5, 8, 2, 9};
        System.out.println(Arrays.toString(arraysDifference(arr1,arr2)));
    }

    private static void division(Integer a, int b) {
        System.out.printf("a / b = %d\n", a / b);
    }

    public static int[] arraysDifference(int[] arr1, int[] arr2) {
        int[] resultArr = new int[0];
            resultArr = new int[Math.max(arr1.length,arr2.length)];
            for (int i = 0; i < resultArr.length; i++) {
                resultArr[i] = arr1[i] - arr2[i];
        }
        return resultArr;
    }

    public static void div(int a, int b) {
        System.out.printf("a / b = %d\n", a / b);
    }
}
