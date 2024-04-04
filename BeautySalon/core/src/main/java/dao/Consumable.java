package dao;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "consumables")
@Data
public class Consumable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "measure")
    @Enumerated(EnumType.STRING)
    private Measures measure;
    @Column(name = "price")
    private double price;
    @ManyToMany(/*mappedBy = "consumables", */ cascade = CascadeType.ALL)
    @JoinTable(name = "service_consumable",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "consumable_id"))
    private List<Servize> services;
}
