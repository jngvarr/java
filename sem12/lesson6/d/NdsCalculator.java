package ru.gb.lesson6.d;

public class NdsCalculator {

    private NdsResolver ndsResolver;

    public NdsCalculator(NdsResolver ndsResolver) {
        this.ndsResolver = ndsResolver;
    }

    public double calculateWithNds(double price) {
        return price * ndsResolver.currentNds();
    }

}
