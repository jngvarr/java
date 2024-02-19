package ru.gb.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
