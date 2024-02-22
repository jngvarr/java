package ru.gb.payment.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.payment.model.Account;
import ru.gb.payment.repositories.AccountRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getBuyerAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("User account not found"));
    }

    public Account getShopAccount() {
        return accountRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("User account not found"));
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

}