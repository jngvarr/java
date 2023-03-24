package ru.gb.lesson6.oc;

import java.time.LocalDate;

public class StandardConverter implements LocalDateConverter {

    @Override
    public String convert(LocalDate date) {
        return date.toString();
    }
}
