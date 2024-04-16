package dao.entities;

import dao.converters.UnitsConverter;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "consumables")
@Data
public class Consumable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "unit")
//    @Convert(attributeName = "name")
    @Convert(converter = UnitsConverter.class)
    private Unit unit;
    @Column(name = "price")
    private Double price;
//    @JsonIgnore
//    @ManyToMany(/*mappedBy = "consumables", */ cascade = CascadeType.ALL)
//    @JoinTable(name = "service_consumable",
//            joinColumns = @JoinColumn(name = "consumable_id"),
//            inverseJoinColumns = @JoinColumn(name = "service_id"))
//    private List<Servize> services;
}
