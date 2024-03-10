package ru.jngvarr.clientmanagement.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

//@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "contact")
    private String contact;

    @Column(name = "dob")
    private Date dob;
}

