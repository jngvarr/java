public class Dwarves extends Player {
    public Dwarves() {
        super(String.format("Dwarf #%d", ++Dwarves.id));
        this.hp = (Dwarves.rnd.nextDouble(150, 200));
        this.damage = Dwarves.rnd.nextDouble(50, 75);
    }
}
