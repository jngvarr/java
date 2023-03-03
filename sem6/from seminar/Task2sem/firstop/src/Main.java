//package ru.gb;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        // Кот (имя, аппетит, сытность)
        // Тарелка (содержит какое-то количество еды)
        // Кот ест из тарлеки. Если в тарелке недостаточно еды - кот ее не трогает

        // ДЗ:
        // while (true) -> while (!plate.isEmpty())
        // 1. Создать массив котов. Пусть все коты из массива по очереди едят из одной тарелки.
        // В конце каждого цикла мы досыпаем в тарелку еду.
        // Для досыпания еды сделать метод increaseFood в классе Plate.
        // 2. Поменять поле satiety у кота с boolean на int.
        // Допустим у кота аппетит 10, сытость 3. Значит кот захочет поесть 7 единиц.
        // 3. * Доработать поток thread в классе Cat, чтобы он каждую секунду уменьшал сытость кота на 1.

        Cat murzik = new Cat("Murzik", 15);
        Cat barsik = new Cat("Barsik",10); // appetite = 10
        Cat vaska = new Cat("Vaska",10); //appetite = 10
        Cat dymka = new Cat("Dymka", 7);
        Cat milka = new Cat("Milka", 4);
        Cat pushistik = new Cat("Pushstik", 5);
        Cat kitty = new Cat("Kitty", 4);


        Cat[] Cats = new Cat[]{murzik, barsik, vaska, dymka, milka, pushistik, kitty};

        Plate plate = new Plate(100, 100);

        while (true) {
            for (Cat cat : Cats) {
                System.out.println(plate);
//                cat.makeALittleBitHungry();
                cat.eat(plate);
                System.out.println(cat);
//                Thread.sleep(1000);
                plate.increaseFood(20);
                System.out.println(plate);
            }
        }

    }
}