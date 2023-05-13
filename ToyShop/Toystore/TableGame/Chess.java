package Toystore.TableGame;

public class Chess extends TableGame{
    public static int weight;
    @Override
    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
           Chess.weight =weight;
    }
}
