package ru.gb.hw52.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String password;
    private String email;
    private String role;
//    @OneToMany
//    @JoinColumn(name = "project_id")
    private Project project;
}
