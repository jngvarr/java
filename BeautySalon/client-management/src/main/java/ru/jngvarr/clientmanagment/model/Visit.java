package ru.jngvarr.clientmanagment.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.jngvarr.servicesmanagement.model.Servize;
import java.util.ArrayList;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class Visit {
    private Date visitDate;
    private ArrayList<Servize> services;
}
