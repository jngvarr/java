package Toystore;

public abstract class Toy {
    static {
        Toy.number = 0;
    }

    protected String ID;
    protected String title;
    protected static int number;
    protected double weight;

    public Toy() {
        this.ID = String.format("#%s", ++Toy.number);
        this.title = (this.getClass().getSimpleName());
    }

    public Toy(String ID, String title) {
        this.ID = ID;
        this.title = title;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(int weight){
        this.weight=weight;
    }
}
