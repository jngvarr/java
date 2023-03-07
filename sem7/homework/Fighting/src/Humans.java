public class Humans extends Player {
    public Humans() {
        super(String.format("Human #%d", ++Humans.id));
        this.hp = (Dwarves.rnd.nextInt(120, 180));
        this.damage = 60;
        this.agility = 17;
        this.defence = 65;
    }
}