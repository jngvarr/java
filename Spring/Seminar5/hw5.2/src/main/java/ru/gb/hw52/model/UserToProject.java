package ru.gb.hw52.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class UserToProject extends EntityWithRelation{
    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private Long userId;

    @OneToOne(targetEntity = Project.class)
    @JoinColumn(name = "project_id")
    private Long projectId;

}
