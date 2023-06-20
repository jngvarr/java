public class LinkedList {
    private Node root;
    private int size;
    private Node tail;

    public void add(int value) {
        Node newNode = new Node(value, null);
        if (root == null) {
            root = newNode;
            size = 1;
            return;
        }
        Node currentNode = root;
        while (currentNode.next != null)
            currentNode = currentNode.next;
        currentNode.next = newNode;
        size++;
    }

    public boolean removeAt(int index) {
        if (size <= index)
            return false;
        if (index == 0) {
            root = root.next;
            size--;
            return true;
        }
        Node currentNode = root;
        for (int i = 0; i < index - 1; i++)
            currentNode = currentNode.next;
        Node prev = currentNode;
        Node next = currentNode.next.next;
        prev.next = next;
        size--;
        return true;
    }

    public boolean remove(int value) {
        if (size == 0)
            return false;
        Node currentNode = root;

        for (int i = 0; currentNode != null; i++) {
            if (currentNode.value == value) {
                this.removeAt(i);
                return true;
            }
            currentNode = currentNode.next;
        }
        return false;
    }

    public boolean removeAll(int value) {
        if (size == 0)
            return false;
        boolean restart = true;
        boolean isDeleted = false;
        while (restart) {
            restart = remove(value);
            if (restart == true)
                isDeleted = true;
        }
        return isDeleted;
    }

    public void reverse() {
        if (size < 2 || root.next == null)
            return;
        else {
            tail = root;
            Node currentNode = root.next;
            root.next = null;
            while (currentNode != null) {
                Node next = currentNode.next;
                currentNode.next = root;
                root = currentNode;
                currentNode = next;
            }
        }
    }


    public void print() {
        System.out.print("[ ");
        Node currentNode = root;
        while (currentNode != null) {
            System.out.print(currentNode.value + " ");
            currentNode = currentNode.next;
        }
        System.out.println("]");
    }

    public int find(int value) {
        if (size == 0)
            return -1;
        Node currentNode = root;
        for (int i = 0; currentNode != null; i++) {
            if (currentNode.value == value)
                return i;
            currentNode = currentNode.next;
        }
        return -1;
    }

    public void sort() {
        this.quickSort(0, size - 1);
    }

    private void quickSort(int leftBorder, int rightBorder) {
        int leftMarker = leftBorder;
        int rightMarker = rightBorder;
        int pivot = this.getValue((leftMarker + rightMarker) / 2);
        do {
            while (this.getValue(leftMarker) < pivot)
                leftMarker++;
            while (this.getValue(rightMarker) > pivot)
                rightMarker--;
            if (leftMarker <= rightMarker) {
                if (leftMarker < rightMarker)
                    this.swap(leftMarker, rightMarker);
                leftMarker++;
                rightMarker--;
            }
        } while (leftMarker <= rightMarker);
        if (leftMarker < rightBorder)
            quickSort(leftMarker, rightBorder);
        if (leftBorder < rightMarker)
            quickSort(leftBorder, rightMarker);
    }

    public int getValue(int index) {
        if (index < 0 || index >= size)
            throw new ArrayIndexOutOfBoundsException();
        Node currentNode = root;
        for (int i = 0; i < index; i++)
            currentNode = currentNode.next;
        return currentNode.value;
    }

    public void swap(int index_1, int index_2) {
        if (index_1 >= size || index_2 >= size || index_1 == index_2 || index_1 < 0 || index_2 < 0)
            return;
        Node node_1 = null;
        Node node_2 = null;
        Node currentNode = root;
        for (int i = 0; node_1 == null || node_2 == null; i++) {
            if (i == index_1)
                node_1 = currentNode;
            else if (i == index_2)
                node_2 = currentNode;
            currentNode = currentNode.next;
        }
        int temp = node_1.value;
        node_1.value = node_2.value;
        node_2.value = temp;
    }

    private class Node {
        public int value;
        public Node next;

        public Node() {
            value = 0;
            next = null;
        }

        public Node(int _value, Node _next) {
            this.value = _value;
            this.next = _next;
        }
    }
}

