package ru.gb.hw52.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class UsersProject extends EntityWithRelation{
    private Long projectId;
    private Long userId;

}
