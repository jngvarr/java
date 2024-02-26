package ru.gb.payment.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gb.payment.model.Account;
import ru.gb.payment.model.dto.Transaction;

@Service
@RequiredArgsConstructor
public class PaymentService {


    private final AccountService accountService;

    /**
     * Проведение транзакции.
     *
     * @param transaction объект с данными для транзакции.
     */
    @Transactional
    public void processPayment(Transaction transaction) {
        Account buyerAccount = accountService.getBuyerAccount(transaction.getBuyerAccountId());
        if ((buyerAccount.getBalance().compareTo(transaction.getSum())) < 0) {
            throw new RuntimeException("Недостаточно средств!");
        }
        Account shopAccount = accountService.getShopAccount();

        buyerAccount.setBalance(buyerAccount.getBalance().subtract(transaction.getSum()));
        shopAccount.setBalance(shopAccount.getBalance().add(transaction.getSum()));

        accountService.save(buyerAccount);
        accountService.save(shopAccount);
    }

    /**
     * Откат произведенной транзакции.
     *
     * @param transaction объект с данными для транзакции.
     */
    @Transactional
    public void rollbackTransaction(Transaction transaction) {
        Account buyerAccount = accountService.getBuyerAccount(transaction.getBuyerAccountId());
        Account shopAccount = accountService.getShopAccount();

        shopAccount.setBalance(
                shopAccount.getBalance().subtract(transaction.getSum()));
        buyerAccount.setBalance(
                buyerAccount.getBalance().add(transaction.getSum()));

        accountService.save(buyerAccount);
        accountService.save(shopAccount);
    }
}

