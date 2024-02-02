package ru.gb.hw52.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gb.hw52.model.Project;
import ru.gb.hw52.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> getByProjectId(Long userId);
}
