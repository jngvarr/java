

public class Render {
    public void render(Object object) {

        if (object instanceof Building) {
            System.out.print(object.getClass()+"\nHealth: " );
            display(object.currentHealthPoint,object.maxHealthPoint);
        } else if (object instanceof Creature) {
            System.out.print(object.getClass()+"\nHealth: " );
            display(object.currentHealthPoint, object.maxHealthPoint);
            System.out.print("Energy: ");
            display(object.currentEnergy, object.maxEnergy);
        }
    }

    private void display(int currentValue, int maxValue) {
        String color;
        if ((currentValue*1.0/maxValue)*100 < 25) {
            color = "\u001B[31m";  // RED
        } else if ((currentValue*1.0/maxValue)*100 > 50) {
            color = "\u001B[32m"; // GREEN
        } else color = "\u001B[33m"; // YELLOW
        String health = "";
        for (int i = 0; i < maxValue; i++) {
            health = i < currentValue ? health + "*" : health + "-";
        }
        System.out.println(color + "[" + health + "]" + "\u001B[0m");
    }
}


//    public static final String ANSI_RESET = "\u001B[0m";
//    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_RED = "\u001B[31m";
//    public static final String ANSI_BLACK = "\u001B[30m";
//      public static final String ANSI_YELLOW = "\u001B[33m";