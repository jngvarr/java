public class Notebook implements Comparable<Notebook> {
    private int price;
    private int ram;
    static int count;
    public Notebook(int price, int ram) {
        this.price = price;
        this.ram = ram;
        int num = ++count;
    }

    public String getInfo() {
        return String.format("Price= %d, RAM = %d", price, ram);
    }

    @Override
    public String toString() {
        return String.valueOf("Price =\t" + price + ", Ram =\t" + ram + "\n");
    }

    @Override
    public int compareTo(Notebook n) {
        return n.price - this.price;
    }

    public int reverseCompareTo(Notebook n) {
        return this.price - n.price;
    }
}
