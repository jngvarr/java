package dao.entities.people;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
//@Data
@Entity
@Table(name = "clients")
//@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends SomeOne{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "first_name")
//    private String firstName;
//
//    @Column(name = "last_name")
//    private String lastName;
//
//    @Column(name = "contact")
//    private String contact;
//
//    @Column(name = "dob")
//    private LocalDate dob;
}

