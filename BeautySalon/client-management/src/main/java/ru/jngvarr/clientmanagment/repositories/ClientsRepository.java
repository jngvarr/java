package ru.jngvarr.clientmanagment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.jngvarr.clientmanagment.model.Client;
@Repository
public interface ClientsRepository extends JpaRepository<Client, Long> {

}
