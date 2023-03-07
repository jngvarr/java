import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Player elf;
        Player dwarf;
        Player human;

        System.out.println("\nChose combat type: \n" +
                "Elf vs Dwarf - 1\n" +
                "Dwarf vs Human - 2\n" +
                "Human vs Elf - 3\n" +
                "KuchaMala - 0");

        Scanner scn = new Scanner(System.in);
        switch (scn.nextInt()) {
            case 1:
                elf = new Elves();
                dwarf = new Dwarves();
                Melee(elf, dwarf);
                break;
            case 2:
                dwarf = new Dwarves();
                human = new Humans();
                Melee(dwarf, human);
                break;
            case 3:
                human = new Humans();
                elf = new Elves();
                Melee(human, elf);
                break;
            case 0:
                KuchaMala();

        }
    }

    public static void Melee(Player creature1, Player creature2) {
        System.out.println(creature1.getInfo());
        System.out.println(creature2.getInfo());
        System.out.println("______________________________");
        boolean combat = true;
        while (combat) {
            if ((creature1.hp > 0) && (creature2.hp > 0)) {
                creature1.Attack(creature2);
                creature2.Attack(creature1);
                System.out.println("-----------ROUND--------------------");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else combat = false;
        }
        System.out.println("______________________________");
    }

    public static void KuchaMala() {
        int horde = 15;
        int elves = 0;
        int dwarves = 0;
        int humans = 0;

        ArrayList<Player> warriors = new ArrayList<>();
        for (int i = 0; i < horde; i++) {
            switch (Player.rnd.nextInt(1, 4)) {
                case 1:
                    warriors.add(new Elves());
                    elves++;
                    break;
                case 2:
                    warriors.add(new Dwarves());
                    dwarves++;
                    break;
                case 3:
                    warriors.add(new Humans());
                    humans++;
            }//isinstanceof
        }
        System.out.printf("Elves: %s\nDwarves: %s\nHumans: %s", elves, dwarves, humans);

//        boolean combat = true;
        while (warriors.size() != 1) {
            for (int i = 0; i < warriors.size(); i++) {
                Player attacktedWarrior = warriors.get(Player.rnd.nextInt(0, warriors.size()));
                warriors.get(i).Attack(attacktedWarrior);
                if (attacktedWarrior.hp <= 0) warriors.remove(attacktedWarrior);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        System.out.println(warriors.get(0).name + "The KUCHAMALA WINNER!!");
    }
}
