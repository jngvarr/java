public class Creature implements Tiredness, HasHealthPoint  {
    private final int currentEnergy;
    private final int maxEnergy;
    private final int maxHealthPoint;
    private final int currentHealthPoint;

    public Creature(int maxEnergy, int currentEnergy, int maxHealthPoint, int currentHealthPoint) {
        this.currentEnergy = currentEnergy;
        this.maxEnergy = maxEnergy;
        this.maxHealthPoint = maxHealthPoint;
        this.currentHealthPoint = currentHealthPoint;
    }

    @Override
    public int getMaxHealthPoint() {
        return maxHealthPoint;
    }

    @Override
    public int getCurrentHealthPoint() {
        return currentHealthPoint;
    }

    @Override
    public int getMaxEnergy() {
        return maxEnergy;
    }

    @Override
    public int getCurrentEnergy() {
        return currentEnergy;
    }
}
