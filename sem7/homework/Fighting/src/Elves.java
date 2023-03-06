
public class Elves extends Player {

    public Elves() {
        super(String.format("Elf #%d", ++Elves.id));
        this.hp = (Elves.rnd.nextDouble(100, 150));
        this.damage = Elves.rnd.nextDouble(25, 50);
    }
}
