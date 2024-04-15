package dao.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
//import ru.jngvarr.storagemanagement.model.Comsunable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ServiceDto {
    private Long id;
    private String title;
    private BigDecimal price;
    private Integer serviceDurationInMinutes;
    private String description;
    private List<String> consumables;
}