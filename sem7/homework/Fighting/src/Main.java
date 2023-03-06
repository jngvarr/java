import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Player elf = new Elves();
        System.out.println(elf.getInfo());
        Player dwarf = new Dwarves();
        System.out.println(dwarf.getInfo());
        Player human = new Human();
        System.out.println(human.getInfo());

        System.out.println("\nChose combat type: \n" +
                "Elf vs Dwarf - 1\n" +
                "Dwarf vs Human - 2\n" +
                "Human vs Elf - 3\n" +
                "KuchaMala - 0");

        Scanner scn = new Scanner(System.in);
        switch (scn.nextInt()) {
            case 1:
            case 2:
            case 3:
            case 0:
        }

        boolean combat = true;
        while (combat) {
            if ((elf.hp > 0) && (dwarf.hp > 0)) {
                elf.Attack(dwarf);
                dwarf.Attack(elf);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else combat = false;
        }
    }
}