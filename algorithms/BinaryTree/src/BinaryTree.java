import java.util.ArrayList;
import java.util.List;

public class BinaryTree<T extends Comparable<T>> {
    Node root;
    public boolean add(T value) {
        if (root == null) {
            root = new Node();
            root.value = value;
            root.color = Color.Black;
            return true;
        }
        return addNode(root, value);
    }

    private boolean addNode(Node node, T value) {
        if (node.value.compareTo(value) == 0)
            return false;
        if (node.value.compareTo(value) > 0) {
            if (node.left != null) {
                boolean result = addNode(node.left, value);
                node.left = rebalanced(node.left);
                return result;
            } else {
                node.left = new Node();
                node.left.color = Color.Red;
                node.left.value = value;
                return true;
            }
        } else {
            if (node.right != null) {
                boolean result = addNode(node.right, value);
                node.right = rebalanced(node.right);
                return result;
            } else {
                node.right = new Node();
                node.right.color = Color.Red;
                node.right.value = value;
                return true;
            }
        }
    }

    private Node rebalanced(Node node) {
        Node result = node;
        boolean needRebalance;
        do {
            needRebalance = false;
            if (result.right != null && result.right.color == Color.Red
                    && (result.left == null || result.left.color == Color.Black)) {
                needRebalance = true;
                result = rightSwap(result);
            }
            if (result.left != null && result.left.color == Color.Red
                    && result.left.left != null && result.left.left.color == Color.Red) {
                needRebalance = true;
                result = leftSwap(result);
            }
            if (result.left != null && result.left.color == Color.Red
                    && result.right != null && result.right.color == Color.Red) {
                needRebalance = true;
                colorSwap(result);
            }
        } while (needRebalance);
        return result;
    }
    private void colorSwap(Node node) {
        node.right.color = Color.Black;
        node.left.color = Color.Black;
        node.color = Color.Red;
    }
    private Node leftSwap(Node node) {
        Node left = node.left;
        Node between = left.right;
        left.right = node;
        node.left = between;
        left.color = node.color;
        node.color = Color.Red;
        return left;
    }
    private Node rightSwap(Node node) {
        Node right = node.right;
        Node between = right.left;
        right.left = node;
        node.right = between;
        right.color = node.color;
        node.color = Color.Red;
        return right;
    }
    private class Node {
        T value;
        Node left;
        Node right;
        Color color;
    }
    public boolean contain(T value) {
        Node currentNode = root;
        while (currentNode != null) {
            if (currentNode.value.equals(value))
                return true;
            if (currentNode.value.compareTo(value) > 0)
                currentNode = currentNode.left;
            else
                currentNode = currentNode.right;
        }
        return false;
    }
}
enum Color {Red, Black};