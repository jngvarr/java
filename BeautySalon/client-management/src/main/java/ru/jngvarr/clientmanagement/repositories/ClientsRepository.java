package ru.jngvarr.clientmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.jngvarr.clientmanagement.model.Client;

import java.util.List;

@Repository
public interface ClientsRepository extends JpaRepository<Client, Long> {

//    @Query(value = "SELECT client FROM Client client WHERE client.contact = :phoneNumber")
    Client findByContact(String contact);
}
//    @Modifying
//    @Query("UPDATE Task SET status = :status WHERE id = :id")
//    void changeStatus(long id, TaskStatus status);
//}
