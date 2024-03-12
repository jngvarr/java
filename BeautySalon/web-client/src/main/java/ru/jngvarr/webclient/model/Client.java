package ru.jngvarr.webclient.model;

import java.util.Date;

public record Client(Long id, String firstName, String lastName, String contact, Date dob) {
}
