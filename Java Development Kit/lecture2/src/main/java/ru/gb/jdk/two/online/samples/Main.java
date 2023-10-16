package ru.gb.jdk.two.online.samples;

public class Main {
    public static class Minotaurus implements Human, Bull {
        @Override
        public void walk() {
            System.out.println("Walk on tow legs");
        }

        @Override
        public void talk() {
            System.out.println("Asks you a riddle");
        }
    }

    public static void main(String[] args) {
        Bull minos0 = new Minotaurus();
        Human minos1 = new Minotaurus();
        Minotaurus minos = new Minotaurus();
        Ox ox0 = new Ox();
        Bull ox2 = new Ox();
        Human man1 = new Man();

        Bull[] allBulls = {ox2, minos0, minos};
        Human[] allHumans = {minos1, man1, minos};


        Man man0 = new Man();
    }
}
