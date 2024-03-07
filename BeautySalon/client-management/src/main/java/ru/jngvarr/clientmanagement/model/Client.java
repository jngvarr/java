package ru.jngvarr.clientmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import ru.jngvarr.beautysalon.common.model.SomeOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Client extends SomeOne{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
