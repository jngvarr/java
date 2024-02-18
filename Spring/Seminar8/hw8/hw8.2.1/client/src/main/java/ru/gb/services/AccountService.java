package ru.gb.services;

import gb.hw82.model.Account;
import gb.hw82.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getUserAccount(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("User account not found"));
    }

    public Account getShopAccount() {
        return accountRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("User account not found"));
    }
}

