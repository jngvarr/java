package Toystore.TableGame;

public class RPG extends TableGame{
    private int weight = 55;

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
        this.weight=weight;
    }
}
