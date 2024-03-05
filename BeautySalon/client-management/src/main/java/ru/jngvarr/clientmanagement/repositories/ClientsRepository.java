package ru.jngvarr.clientmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jngvarr.clientmanagement.model.Client;
@Repository
public interface ClientsRepository extends JpaRepository<Client, Long> {

}
