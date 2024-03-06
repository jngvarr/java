package ru.jngvarr.clientmanagement.model;

import ru.jngvarr.beautysalon.common.model.SomeOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class Client extends SomeOne{
    private Long id;
    private ArrayList<Visit> visits;
}
