package ru.jngvarr.clientmanagement.repositories;

import dao.entities.people.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientsRepository extends JpaRepository<Client, Long> {

//    @Query(value = "SELECT client FROM Client client WHERE client.contact = :phoneNumber")
    Client findByContact(String contact);
}
//    @Modifying
//    @Query("UPDATE Task SET status = :status WHERE id = :id")
//    void changeStatus(long id, TaskStatus status);
//}
