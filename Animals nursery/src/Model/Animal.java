package Model;

public abstract class Animal {
    static {
        Animal.number = 0;
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
        return Animal.number;
    }

    public static void setNumber(int number) {
        Animal.number = number;
    }

    public String getDayOfBirth() {
        return dayOfBirth;
    }

    public String getCommands() {
        return commands;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return (ID + " " + name + " " + dayOfBirth + " " + commands + " " + type);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDayOfBirth(String dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public void setType(String type) {
        this.type = type;
    }
}

