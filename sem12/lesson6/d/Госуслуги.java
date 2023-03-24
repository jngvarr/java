package ru.gb.lesson6.d;

public class Госуслуги implements NdsResolver {

    @Override
    public double currentNds() {
        return сегодняшнийНдс();
    }

    public double сегодняшнийНдс() {
        return 1.3;
    }

}
