package Ex007;

public class Magician extends BaseHero {

    private int mana;
    private int maxMana;

    public Magician() {
        super(String.format("Hero_Magician #%d", ++Magician.number),
                Magician.r.nextInt(100, 200));
        this.maxMana = Magician.r.nextInt(50, 150);
        this.mana = maxMana;
    }

    public int Attack() {
        int damage = BaseHero.r.nextInt(20, 30);
        this.mana -= (int) (damage * 0.8);
        if (mana < 0) {
            this.mana += (int) (damage * 0.8);
            return 0;
        } else return damage;
    }

    public int Heal(BaseHero target) {
        int heal = BaseHero.r.nextInt(20) * this.mana / 2 / 100;
        this.mana -= mana;
        target.GetHealed(heal);
        if (mana < 0) return 0;
        else return heal;

    }

    public String getInfo() {
        return String.format("%s  Mana: %d", super.getInfo(), this.mana);
    }
}