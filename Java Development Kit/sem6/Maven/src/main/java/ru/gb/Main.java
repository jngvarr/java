package ru.gb;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class Main {
    public static void main(String[] args) {
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
        descriptiveStatistics.addValue(2);
        descriptiveStatistics.addValue(3);
        descriptiveStatistics.addValue(4);
        descriptiveStatistics.addValue(5);

        System.out.println(descriptiveStatistics.getMax());
        System.out.println(descriptiveStatistics.getMin());
        System.out.println(descriptiveStatistics.getMean());
        System.out.println(descriptiveStatistics.getSum());

        System.out.println(ArithmeticUtils.factorial(5));
        System.out.println(CombinatoricsUtils.factorial(5));
        System.out.println(ArithmeticUtils.lcm(16, 24)); // наименьшеее общее частное
        System.out.println(ArithmeticUtils.gcd(16, 24)); // наибольший общий делитель
        System.out.println(ArithmeticUtils.isPowerOfTwo(16));
    }
}