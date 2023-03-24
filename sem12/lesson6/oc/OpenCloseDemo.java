package ru.gb.lesson6.oc;

public class OpenCloseDemo {

    public static void main(String[] args) {
        CurrentDatePrinter currentDatePrinter = new CurrentDatePrinter();
        currentDatePrinter.printCurrentDate(new StandardConverter()); // yyyy-MM-dd
        currentDatePrinter.printCurrentDate(new MyCustomConverter()); // dd.MM.yy
    }

}