package ru.jngvarr.webclient.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Client {
    private Long id;
    private String firstName;
    private String lastName;
    private String contact;
    private LocalDate dob;
}
