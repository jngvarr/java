package dao.entities.people;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "employees")
public class Employee extends SomeOne {
    @Column
    @Enumerated(EnumType.STRING)
    private Function function;
}
