package dao.people;

import jakarta.persistence.*;

import java.time.LocalDate;

@MappedSuperclass
public class SomeOne {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "contact")
    private String contact;

    @Column(name = "dob")
    private LocalDate dob;
}
