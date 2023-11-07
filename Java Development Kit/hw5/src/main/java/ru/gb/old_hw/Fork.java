package ru.gb.old_hw;

public class Fork {
    private boolean fork;
    static int num;
    int forkNumber;

    public Fork() {
        this.forkNumber =  num++;
    }

    public int getForkNumber() {
        return forkNumber;
    }

    public boolean isTaken() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;

    }
}