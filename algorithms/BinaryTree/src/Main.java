public class Main {
    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();

        tree.add(4);
        tree.add(7);
        tree.add(8);
        tree.add(9);
        tree.add(1);

        for (int i = 0; i < 10; i++) {
            System.out.println(i + " " + tree.contain(i));
        }
    }
}