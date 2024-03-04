package ru.jngvarr.beautysalon.model.people;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.jngvarr.beautysalon.model.salon_services.Servize;

import java.util.ArrayList;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class Visit {
    private Date visitDate;
    private ArrayList<Servize> services;
}
