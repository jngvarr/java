package dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Consumable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "measure")
    @Enumerated
    private Measures measure;
    @Column(name = "price")
    private double price;

//    private Service service;
}
