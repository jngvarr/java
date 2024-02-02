package ru.gb.hw52.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "user_name")
    private String userName;
    private String password;
    private String email;
    private String role;
//    @OneToOne
//    @JoinColumn(name = "user_id")
//    private UsersProject uProject;
}
