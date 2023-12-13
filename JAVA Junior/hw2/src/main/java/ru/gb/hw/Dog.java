package ru.gb.hw;

import ru.gb.hw.Animal;

public class Dog extends Animal {
    String nickName;
    int age;

    public Dog(String nickName, int age) {
        this.nickName = nickName;
        this.age = age;
    }

    public String toBark(){
        return "Гав-гав!";
    }
}
