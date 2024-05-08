package dao.entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
//import ru.jngvarr.storagemanagement.model.Comsunable;

@Data
@Entity
@Table(name = "services")
@AllArgsConstructor
@RequiredArgsConstructor
public class Servize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "duration")
//    @JsonProperty("duration")
    private Integer serviceDurationInMinutes;
    @Column(name = "description")
    private String description;
    @OneToMany
    @JoinTable(name = "service_to_consumables",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "consumable_id"))
    private List<Consumable> consumables;
}