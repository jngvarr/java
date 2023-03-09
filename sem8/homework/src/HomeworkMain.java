

public class HomeworkMain {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLACK = "\u001B[30m";

    public static void main(String[] args) {
        // Представьте, что вы пишете класс Reder, который отвечает за вывод на экран текущего уровня жизней и усталости какого-то объекта.
        // (Подразумеваем, что вывод на экран - это просто печать в консоль)
        // У класса есть 1 метод, который принимает тип Object и делает следующее:
        // 1. Если object типа HasHealthPoint, то выводим его уровень жизни
        // 2. Если object типа Tiredness, то выводим его уровень усталости
        // При этом текст значения должен иметь цвет в соответствии с правилом:
        // BLACK(0, 24), RED(25, 50), GREEN(51-100)
        // 3. Создать несколько классов:
        // 3.1 Здание. Имеет только жизни.
        // 3.2 Персноаж. Имеет и жизни, и усталость

//        System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);
        System.out.println("[-------------------------------------------------------------------------------                                         ]");

        Building building = new Building(100, 40);

        Render render = new Render();
        render.render(building); // 40 - написано красным цветом
    }

    static class Render {

        public void render(Object object) {
            // TODO: 06.03.2023 Добавить реализацию
            if (object instanceof HasHealthPoint hasHealthPoint) {

            }
        }

    }

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

    static class Building implements HasHealthPoint {

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


}
