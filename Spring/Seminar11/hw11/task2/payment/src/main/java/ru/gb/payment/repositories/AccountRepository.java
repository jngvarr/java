package ru.gb.payment.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.payment.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
