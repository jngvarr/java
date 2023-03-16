package ru.gb.homework.src;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

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

    public static Comparator<Notebook> twoPositionComparator = new Comparator<Notebook>() {
        @Override
        public int compare(Notebook n1, Notebook n2) {
            if (n1.ram < n2.ram) {
                return 1;
            } else if (n2.price == n1.price) {
                if (n1.price == n2.price) {
                    return 0;
                } else if (n1.price < n2.price) {
                    return 1;
                }
            }
            return -1;
        }
    };

    public static void main(String[] args) {
        Comparator<Notebook> notebookPriceComparator = (o1, o2) -> o2.price - o1.price;
        Comparator<Notebook> notebookScDiagComparator = (o1, o2) -> (int)((o1.screenDiagonal - o2.screenDiagonal)*10);


        int[] ram = {8, 12, 16, 24, 32, 64, 128};
        double[] diagonalSize = {13.3, 14.0, 15.6, 16.1, 17.3};
        List<Notebook> notebooks = new ArrayList<>();
        for (int i = 0; i < rnd.nextInt(3, 10); i++) {
            notebooks.add(new Notebook(rnd.nextInt(50000, 100000), ram[rnd.nextInt(ram.length)],
                    diagonalSize[rnd.nextInt(diagonalSize.length)]));
        }
        for (Notebook n : notebooks) {
            Iterator<String> notebookParameters = n; //приведение экземпляра n к типу Iterator
            while (notebookParameters.hasNext()) {
                System.out.println(n.next());
            }
            System.out.println("--------------------------------------------------------");
        }
        System.out.println(notebooks);
//      notebooks.sort(Notebook::compareTo);
//      Collections.sort(notebooks,notebookPriceComparator);
//      notebooks.sort(notebookPriceComparator);
        Collections.sort(notebooks, Notebook::compareTo);
        System.out.println("Сортировка по возрастанию цены:");
        System.out.println(notebooks);
//        notebooks.sort(twoPositionsComparator);
        notebooks.sort(notebookPriceComparator.reversed());
        System.out.println("Сортировка по убыванию цены");
        System.out.println(notebooks);
        notebooks.sort(twoPositionComparator);
        System.out.println("Сортировка по по убыванию RAM. Если RAM равны - по убыванию цены:");
        System.out.println(notebooks);
        System.out.println("Сортировка по увеличению диагонали экрана:");
        notebooks.sort(notebookScDiagComparator);
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
   CharSequence
}