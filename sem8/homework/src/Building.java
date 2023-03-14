

public class Building implements HasHealthPoint {
    private final int maxHealthPoint;
    private int currentHealthPoint;

    public Building(int maxHealthPoint, int currentHealthPoint) {
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
}

