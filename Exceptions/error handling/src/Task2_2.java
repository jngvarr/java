//Задание 2
//
public class Task2_2 {
    public static void main(String[] args) {
//        int[] arr = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
//        int[] arr2 = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
//        correctCode(arr);
//        correctCode(arr2);
    }

    public static void correctCode(int[] intArray) {
        try {
            int d = 0;
            double catchedRes1 = intArray[8] / d;
            System.out.println("catchedRes1 = " + catchedRes1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Catching exception: " + e.getClass().getSimpleName());
        } catch (
                ArithmeticException e) {
            System.out.println("Catching exception: " + e.getClass().getSimpleName());
        }
    }
}
