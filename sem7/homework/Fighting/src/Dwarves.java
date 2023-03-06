public class Dwarves extends Player {
    public Dwarves() {
        super(String.format("Dwarf #%d", ++Dwarves.id));
        this.hp = (Dwarves.rnd.nextInt(150, 200));
        this.damage = 75;
//        this.damage = Dwarves.rnd.nextInt(50, 75);
        this.agility = 5;
        this.defence = 100;
    }
}
