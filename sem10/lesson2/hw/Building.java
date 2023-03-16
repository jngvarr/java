package ru.gb.lesson4.lesson2.hw;

public class Building implements HasHealthPoint {

    private int currentHp;
    private int maxHp;

    @Override
    public int getCurrentHealthPoint() {
        return currentHp;
    }

    @Override
    public int getMaxHealthPoint() {
        return maxHp;
    }

}
