package ru.gb.hw;

import ru.gb.hw.Animal;

public class Dog extends Animal {
    String nickName;
    int age;

    public Dog(String nickName, int age) {
        this.nickName = nickName;
        this.age = age;
    }

    public void toBark(){
        System.out.println("Гав-гав!");
    }
}
