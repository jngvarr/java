package ru.gb.lesson6.oc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MyCustomConverter implements LocalDateConverter {

    @Override
    public String convert(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yy"));
    }
}
