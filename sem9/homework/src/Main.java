import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.Random;

// Создать класс Notebook с полями:
// 1. Стоимость (int)
// 2. Оперативная память (int)
// Нагенерить объектов этого класса, создать список и отсортировать его в трех вариантах:
// 1. По возрастанию цены
// 2. По убыванию цены
// 3. По оперативке по убыванию. Если оперативки равны - по убыванию цены.
// 4.+ придумать свои параметры и отсортировать по ним
public class Main {
    static Random rnd = new Random();

    public static void main(String[] args) {

        int[] ram = {8, 12, 16, 24, 32, 64, 128};
        List<Notebook> notebooks = new ArrayList<>();
        for (int i = 0; i < rnd.nextInt(5, 10); i++) {
            notebooks.add(new Notebook(rnd.nextInt(50000, 100000), ram[rnd.nextInt(ram.length)]));

        }
        System.out.println(notebooks);
        notebooks.sort(Notebook::compareTo);
        System.out.println(notebooks);
        notebooks.sort(Notebook::reverseCompareTo);
        System.out.println(notebooks);

//        for (Notebook n : notebooks) {
//            System.out.println(n.getClass() + ", " + n.getInfo());
//        }
//        notebooks.sort(Notebook::compareTo);
//        System.out.println("__________________________________________________________________________");
//        for (Notebook n : notebooks) {
//            System.out.println(n.getClass() + ", " + n.getInfo());
//        }
//        System.out.println("__________________________________________________________________________");
//        notebooks.sort(Notebook::reverseCompareTo);
//        for (Notebook n : notebooks) {
//            System.out.println(n.getClass() + ", " + n.getInfo());
//        }
    }
}