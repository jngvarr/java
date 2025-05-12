package jngvarr.ru.pto_ackye_rzhd.repositories;

import jngvarr.ru.pto_ackye_rzhd.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, Long> {
}
