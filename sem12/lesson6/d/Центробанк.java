package ru.gb.lesson6.d;

public class Центробанк implements NdsResolver {

    @Override
    public double currentNds() {
        return ндсНаСегодня();
    }

    public double ндсНаСегодня() {
        return 1.2;
    }

}
