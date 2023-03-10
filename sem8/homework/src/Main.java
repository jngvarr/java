public class Main {

    public static void main(String[] args) {

        Building building = new Building(100, 40);
        Creature creature = new Creature(111, 70, 150, 101);
        Creature creature2 = new Creature(111, 25, 150, 30);
        Render render = new Render();
        render.render(building);
        render.render(creature);
        render.render(creature2);

    }


}




