package ru.jngvarr.bot.model;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    @Modifying
//    @Transactional
//    @Query(value = "INSERT INTO users (username, email, password, status) VALUES (:username, :email, :password, :status)", nativeQuery = true)
//    void saveUser(@Param("username") String username,
//                  @Param("email") String email,
//                  @Param("password") String password,
//                  @Param("status") String status);
}
