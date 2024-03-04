package ru.jngvarr.beautysalon.model.people;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class Client extends SomeOne{
    private Long id;
    private ArrayList<Visit> visits;
}
