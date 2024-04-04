package dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    private Integer serviceDurationInMinutes;
    @Column(name = "description")
    private String description;
    @Column(name = "consumables")
    @JsonIgnore
    @ManyToMany(mappedBy = "services")
    private List<Consumable> consumables;
}
