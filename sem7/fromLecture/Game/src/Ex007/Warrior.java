package Ex007;

public class Warrior extends BaseHero {

    private int power;
    private int maxPower;

    public Warrior() {
        super(String.format("Hero_Warrior #%d", ++Warrior.number),
                Ex007.Warrior.r.nextInt(100, 200));
        this.maxPower = Ex007.Magician.r.nextInt(50, 150);
        this.power = maxPower;
    }

    public int Attack() {
        int damage = BaseHero.r.nextInt(40, 60);
        int criticalHit = BaseHero.r.nextInt(50, 100) * damage / 100;
        this.power -= (int) (damage * 0.2);
        if (power < 0) {
            this.power += (int) (damage * 0.2);
            return 0;
        } else {
            if (BaseHero.r.nextInt(4) < 4) {
                this.power += power * 2;
                criticalHit = 0;
            }
            return damage + criticalHit;
        }
    }

    public String getInfo() {
        return String.format("%s  Power: %d", super.getInfo(), this.power);
    }

}

