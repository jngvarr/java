
public class Elves extends Player {

    public Elves() {
        super(String.format("Elf #%d", ++Elves.id));
        this.hp = (Elves.rnd.nextInt(100, 150));
//        this.damage = Elves.rnd.nextInt(25, 50);
        this.damage = 50;
        this.defence = 50;
        this.agility = 30;
    }
}
