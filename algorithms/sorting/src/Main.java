import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int[] arr = createArray();
        printArray(arr);
       // quickSort(arr, 0, arr.length - 1);
        heapSort(arr);
        printArray(arr);

    }

    public static void heapSort(int[] arr) {
        int n = arr.length;

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
            printArray(arr);
        }

        for (int i = n - 1; i >= 0; i--) {
            Main.swap(arr,0,i);
            heapify(arr, i, 0);
        }
    }

    public static void swap(int[] arr, int num1, int num2) {
        int temp = arr[num1];
        arr[num1] = arr[num2];
        arr[num2] = temp;
    }

    private static void heapify(int[] arr, int n, int i) {
        // Нахождение максимального значения
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < n && arr[l] > arr[largest])
            largest = l;

        if (r < n && arr[r] > arr[largest])
            largest = r;

        if (largest != i) {
            Main.swap(arr, i, largest);
            heapify(arr, n, largest);
        }
    }
    public static void quickSort(int[] arr, int leftEdge, int rightEdge) {
        int leftMarker = leftEdge;
        int rightMarker = rightEdge;
        int pivot = arr[(leftEdge + rightEdge) / 2];
        do {
            while (arr[leftMarker] < pivot) leftMarker++;
            while (arr[rightMarker] > pivot) rightMarker--;
            if (leftMarker <= rightMarker) {
                if (leftMarker < rightMarker) {
                    int temp = arr[leftMarker];
                    arr[leftMarker] = arr[rightMarker];
                    arr[rightMarker] = temp;
                }
                leftMarker++;
                rightMarker--;
            }
        } while (leftMarker <= rightMarker);
        if (leftMarker < rightEdge) quickSort(arr, leftMarker, rightEdge);
        if (rightMarker > leftEdge) quickSort(arr, leftEdge, rightMarker);
    }

    public static int[] createArray() {
        int[] arr = new int[20];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new Random().nextInt(20);
        }
        return arr;
    }

    public static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }


}


