package ru.jngvarr.staffmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Employee extends SomeOne {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

}
