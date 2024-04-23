package ru.jngvarr.clientmanagement.repositories;

import dao.entities.people.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientsRepository extends JpaRepository<Client, Long> {

    //    @Query(value = "SELECT client FROM Client client WHERE client.contact = :phoneNumber")
    List<Client> findAllByContact(String contact);

    Client findByLastName(String name);

    void findByFirstName(String name);

//    @Query(value = "SELECT DISTINCT c FROM Client c WHERE c.firstName = :name AND c.lastName = :lastName")
    List<Client>findAllByFirstNameAndLastName(String name, String lastName);

//    @Query(value = "SELECT DISTINCT c FROM Client c WHERE c.firstName = :name")
    List<Client>findAllByFirstName(String name);

    List<Client>findAllByLastName(String lastName);
}
//    @Modifying
//    @Query("UPDATE Task SET status = :status WHERE id = :id")
//    void changeStatus(long id, TaskStatus status);
//}
