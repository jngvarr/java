package dao;

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
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    private BigDecimal price;
    @Column(name = "duration")
    private int serviceDurationInMinutes;
    @Column(name = "description")
    private String description;
    @Column(name = "consumables")
    @ManyToMany(mappedBy = "consumable")
    private List<Consumable> consumables;
}
