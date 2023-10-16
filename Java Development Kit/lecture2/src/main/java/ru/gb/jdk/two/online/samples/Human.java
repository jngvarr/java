package ru.gb.jdk.two.online.samples;

public interface Human {
    default void walk(){
        System.out.println("Walk on two feet");
    };
    public void talk();
}
