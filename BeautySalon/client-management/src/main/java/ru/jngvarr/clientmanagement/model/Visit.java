package ru.jngvarr.clientmanagement.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.Date;

import ru.jngvarr.servicemamagement.model.Servize;
@Data
@RequiredArgsConstructor
public class Visit {
    private Date visitDate;
    private ArrayList<Servize> services;
}
