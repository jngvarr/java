package src;

public class test {

    public static void main(String[] args) {
        Box appleBox = new Box();
    }


    public class Box<T>{
        public void add(Fruit <T> fruit) {

        }
        public int getWeight(){
            return weight;
        }
    }


    public class Fruit {
        private final int weight;

        public Fruit(int weight) {
            this.weight = weight;
        }
    }

    public class Apple extends Fruit {
        public Apple(int weight) {
            super(weight);
        }
    }

    public class Orange extends Fruit {
        public Orange(int weight) {
            super(weight);
        }
    }
}
