package dao.entities.people;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clients")
@AllArgsConstructor
@Builder
public class Client extends SomeOne {
}

