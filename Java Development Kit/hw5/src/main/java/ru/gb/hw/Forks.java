package ru.gb.hw;

public class Forks extends Thread{
    boolean leftFork;
    boolean rightFork;

    private boolean isLeftFork(){
        return leftFork;
    }
    private boolean isRightFork(){
        return rightFork;
    }
    private void setLeftFork(boolean leftFork){
        this.leftFork = leftFork;
    }
    private void setRightFork(boolean rightFork){
        this.leftFork = rightFork;
    }
}
