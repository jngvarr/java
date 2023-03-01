public class Plate {

    private int food;
    private int capacity;

    public Plate(int food, int capacity) {
        this.food = food;
        this.capacity = capacity;
    }

    public boolean decreaseFood(int foodToDecrease) {
        if (food >= foodToDecrease) {
            food = food - foodToDecrease;
            return true;
        }

        return false;
    }

    public boolean increaseFood(int foodToIncrease) {
        if (capacity >= food + foodToIncrease) {
            food = food + foodToIncrease;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Plate[" + food + "]";
    }

}
