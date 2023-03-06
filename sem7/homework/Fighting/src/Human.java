public class Human extends Player {
    public Human() {
        super(String.format("Human #%d", ++Human.id));
        this.hp = (Dwarves.rnd.nextInt(120, 180));
        this.damage = 60;
        this.agility = 17;
        this.defence = 65;
    }
}