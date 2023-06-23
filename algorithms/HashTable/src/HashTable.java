public class HashTable<K, V> {
    private static final int DEFAULT_SIZE = 4;
    private static final double LOAD_FACTOR = 0.75;
    private int size;
    private Bucket<K, V>[] buckets;
    public HashTable() {
        this(DEFAULT_SIZE);
    }
    public HashTable(int _size) {
        if (_size <= DEFAULT_SIZE)
            buckets = new Bucket[DEFAULT_SIZE];
        else
            buckets = new Bucket[_size];
    }
    private int calculatedIndex(K key) {
        return Math.abs(key.hashCode() % buckets.length);
    }
    public V get(K key) {
        int index = calculatedIndex(key);
        Bucket<K, V> bucket = buckets[index];
        if (bucket == null)
            return null;
        return bucket.get(key);
    }
    public boolean add(K key, V value) {
        if (buckets.length * LOAD_FACTOR < size)
            recalculate();
        int index = calculatedIndex(key);
        Bucket<K, V> bucket = buckets[index];
        if (bucket == null) {
            bucket = new Bucket<>();
            buckets[index] = bucket;
        }
        boolean added = bucket.add(key, value);
        if (added)
            size++;
        return added;
    }
    public boolean remove(K key) {
        int index = calculatedIndex(key);
        Bucket<K, V> bucket = buckets[index];
        if (bucket == null)
            return false;
        boolean removed = bucket.remove(key);
        if (removed)
            size--;
        return removed;
    }
    private void recalculate() {
        Bucket<K, V>[] old = buckets;
        buckets = new Bucket[old.length * 2];
        for (int i = 0; i < old.length; i++) {
            Bucket<K, V> bucket = old[i];
            Bucket.Node currentNode = bucket.root;
            while (currentNode != null) {
                this.add((K)currentNode.entity.key, (V)currentNode.entity.value);
                currentNode = currentNode.next;
            }
            old[i] = null;
        }
        old = null;
    }
    public void printValue(K key) {
        System.out.print(this.get(key));
    }
    private class Bucket<K, V> {
        Node root;
        public boolean add(Entity entity) {
            Node newNode = new Node();
            newNode.entity = entity;
            if (root == null) {
                root = newNode;
                return true;
            }
            Node currentNode = root;
            while (currentNode != null) {
                if (currentNode.entity.equals(entity))
                    return false;
                if (currentNode.next == null) {
                    currentNode.next = newNode;
                    return true;
                }
                currentNode = currentNode.next;
            }
            return false;
        }
        public boolean add(K key, V value) {
            Entity entity = new Entity(key, value);
            return this.add(entity);
        }
        public V get(K key) {
            Node currentNode = root;
            while (currentNode != null) {
                if (currentNode.entity.key.equals(key))
                    return currentNode.entity.value;
                else
                    currentNode = currentNode.next;
            }
            return null;
        }
        public boolean remove(K key) {
            if (root == null)
                return false;
            if (root.entity.key.equals(key)) {
                root = root.next;
                return true;
            }
            Node currentNode = root;
            while (currentNode.next != null) {
                if (currentNode.next.entity.key.equals(key)) {
                    currentNode.next = currentNode.next.next;
                    return true;
                }
                currentNode = currentNode.next;
            }
            return false;
        }
        public boolean set(K key, V value) {
            Node currentNode = root;
            while (currentNode != null) {
                if (currentNode.entity.key.equals(key)) {
                    currentNode.entity.value = value;
                    return true;
                }
                else
                    currentNode = currentNode.next;
            }
            return false;
        }
        private class Entity {
            K key;
            V value;

            public Entity() {
            }

            public Entity(K _key, V _value) {
                this.key = _key;
                this.value = _value;
            }
        }
        private class Node {
            Entity entity;
            Node next;
        }
    }
}