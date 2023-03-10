abstract class Object {

    protected int currentEnergy;
    protected int maxEnergy;
    protected int maxHealthPoint;
    protected int currentHealthPoint;

    interface HasHealthPoint {
        int getMaxHealthPoint();

        int getCurrentHealthPoint();

    }

    interface Tiredness {
        // Максимальное значение уровня бодрости объекта
        int getMaxEnergy();

        // Текущее значение уровня бодрости объекта
        int getCurrentEnergy();
    }
}