package ru.gb;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class Main {
    public static void main(String[] args) {
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
<<<<<<< HEAD
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

=======
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
>>>>>>> b9ff8eeafb8cb1ac076552bd0f0bbe4420ffb901
}