package Controller;

import Model.Animals;

public class Counter implements AutoCloseable {
    static int animalCount;

    static {
        animalCount = 0;
    }
    public void add() {
        animalCount++;
    }

    public static int getAnimalCount() {
        return animalCount;
    }

    @Override
    public void close() throws Exception {
        System.out.println("Counter closed");
    }
}
