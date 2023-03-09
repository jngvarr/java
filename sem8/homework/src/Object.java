abstract class Object {
    interface HasHealthPoint {
        int getMaxHealthPoint();

        int getCurrentHealthPoint();

    }

    interface Tiredness {
        // Максимальное значение уровеня бодрости объекта
        int getMaxEnergy();

        // Текущее значение уровеня бодрости объекта
        int getCurrentEnergy();
    }
}