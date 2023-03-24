package ru.gb.lesson6.d;

public class DependencyInversionDemo {

    public static void main(String[] args) {
        double price = 100.0;
        // вычислить цену с учетом НДС

        NdsResolver ndsResolver = new Госуслуги();
        NdsCalculator ndsCalculator = new NdsCalculator(ndsResolver);
        double priceWithNds = ndsCalculator.calculateWithNds(price);
        System.out.println(priceWithNds);

        // KISS Keep It Simple
    }

}
