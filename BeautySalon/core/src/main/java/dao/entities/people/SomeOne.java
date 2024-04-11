package dao.entities.people;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@MappedSuperclass
abstract class SomeOne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "contact")
    private String contact;

    @Column(name = "dob", nullable = true)
    private LocalDate dob;
}
