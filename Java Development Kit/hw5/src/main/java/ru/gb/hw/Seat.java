package ru.gb.hw;

public class Seat {
    private final int number;
    private final Fork left;
    private final Fork right;


    public Seat(int number, Fork left, Fork right) {
        this.number = number;
        this.left = left;
        this.right = right;
    }

    public int getNumber() {
        return number;
    }

    public Fork getLeft() {
        return left;
    }

    public Fork getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Seat seat = (Seat) o;

        if (number != seat.number) return false;
        if (!left.equals(seat.left)) return false;
        return right.equals(seat.right);
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SeatImpl{" +
                "number=" + number +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
