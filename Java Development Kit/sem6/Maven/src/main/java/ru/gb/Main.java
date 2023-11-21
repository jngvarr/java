package ru.gb;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class Main {
    public static void main(String[] args) {
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
        descriptiveStatistics.addValue(2.4);
        descriptiveStatistics.addValue(4.4);
        descriptiveStatistics.addValue(3.3);
        descriptiveStatistics.addValue(3.5);
        descriptiveStatistics.addValue(2.7);
        descriptiveStatistics.addValue(8.4);
        System.out.println("max value " + descriptiveStatistics.getMax());
        System.out.println("min value " + descriptiveStatistics.getMin());
        System.out.println("average value " + descriptiveStatistics.getMean());
        System.out.println("summ " + descriptiveStatistics.getSum());

        System.out.println(ArithmeticUtils.factorial(5));
        System.out.println(CombinatoricsUtils.factorial(5));
        System.out.println(ArithmeticUtils.lcm(5, 7)); // наименьшее общее частное
        System.out.println(ArithmeticUtils.gcd(24 ,16)); // наибольший общий делиель
        System.out.println(ArithmeticUtils.isPowerOfTwo(256)); // проверка числа на степень двойки
    }

}