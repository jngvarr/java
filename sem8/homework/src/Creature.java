public class Creature extends Object implements Object.HasHealthPoint, Object.Tiredness{
    public Creature(int maxEnergy, int currentEnergy, int maxHealthPoint, int currentHealthPoint ){
        this.currentEnergy=currentEnergy;
        this.maxEnergy=maxEnergy;
        this.maxHealthPoint=maxHealthPoint;
        this.currentHealthPoint= currentHealthPoint;

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
