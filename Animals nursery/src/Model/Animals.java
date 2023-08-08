package Model;

public abstract class Animals {
    static {
        Animals.number = 0;
    }

    protected static int number;
    protected String ID;
    protected String name;
    protected String dayOfBirth;
    protected String commands;
    protected String type;


    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public static int getNumber() {
        return Animals.number;
    }

    public static void setNumber(int number) {
        Animals.number = number;
    }

    public String getDayOfBirth() {
        return dayOfBirth;
    }

    public String getCommands() {
        return commands;
    }

    @Override
    public String toString() {
        return (ID + " " + name + " " + dayOfBirth + " " + commands + " " + type);
    }
}

