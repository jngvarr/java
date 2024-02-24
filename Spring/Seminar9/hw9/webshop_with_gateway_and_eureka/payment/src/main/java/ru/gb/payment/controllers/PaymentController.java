package ru.gb.payment.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.payment.model.Account;
import ru.gb.payment.model.dto.Transaction;
import ru.gb.payment.services.AccountService;
import ru.gb.payment.services.PaymentService;

import java.util.List;

/**
 * Контроллер оплаты.
 */
@RestController
@AllArgsConstructor
public class PaymentController {
    /**
     * Сервис оплаты.
     */
    private final PaymentService paymentService;
    private final AccountService accountService;

    @GetMapping()
    public ResponseEntity<List<Account>> getAccounts() {
        return ResponseEntity.ok().body(accountService.getAllAccounts());
    }

    /**
     * Проведение оплаты.
     *
     * @param transaction объект с данными для транзакции.
     * @return ответ с подтверждением.
     */
    @PostMapping()
    public ResponseEntity<Void> transaction(@RequestBody Transaction transaction) {
        paymentService.processPayment(transaction);
        return ResponseEntity.ok().body(null);
    }

    /**
     * Откат произведенной транзакции.
     *
     * @param transaction объект с данными для транзакции.
     * @return ответ с подтверждением.
     */
    @PostMapping("/rollback")
    public ResponseEntity<Void> rollbackTransaction(@RequestBody Transaction transaction) {
        paymentService.rollbackTransaction(transaction);
        return ResponseEntity.ok().body(null);
    }
}
