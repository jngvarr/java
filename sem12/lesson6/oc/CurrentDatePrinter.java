package ru.gb.lesson6.oc;

import java.time.LocalDate;

public class CurrentDatePrinter {

    // 2 ответственности - получать дату и печатать ее В КОНСОЛЬ!!!!
    //

    public void printCurrentDate() {
        printCurrentDate(new StandardConverter());
    }

    public void printCurrentDate(LocalDateConverter converter) {
        LocalDate now = LocalDate.now(); // текущая дата
        String convert = converter.convert(now);
        System.out.println(convert);
    }

}
