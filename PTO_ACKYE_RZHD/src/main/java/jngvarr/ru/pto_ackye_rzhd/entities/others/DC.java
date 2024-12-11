package jngvarr.ru.pto_ackye_rzhd.entities.others;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@RequiredArgsConstructor
public class DC {
    @Id
    private long id;
    private String dcModel;
    private LocalDate manufactureDate;
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "dc_to_meters",
            joinColumns = @JoinColumn(name = "dc_id"),
            inverseJoinColumns = @JoinColumn(name = "meter_id"))
    private List<Meter> meterList;
}
