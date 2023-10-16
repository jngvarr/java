package ru.gb.jdk.two.online.samples;

public class Man implements Human{
    public Man() {
        super();
    }

    @Override
    public void walk() {
        System.out.println("Walk on two feet");
    }
    @Override
    public void talk() {
        System.out.println("Talks meaningful words");
    }
}
