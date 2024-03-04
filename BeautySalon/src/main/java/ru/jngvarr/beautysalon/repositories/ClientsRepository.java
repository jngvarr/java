package ru.jngvarr.beautysalon.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.jngvarr.beautysalon.model.people.Client;

public interface ClientsRepository extends JpaRepository<Client, Long> {

}
