package ru.gb.web_client.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.gb.web_client.model.Transaction;

@FeignClient(name="payment")
public interface PaymentClientApi {
    @PostMapping()
    ResponseEntity<?> pay(@RequestBody Transaction transaction);

    @PostMapping("/rollback")
    ResponseEntity<?> rollback(@RequestBody Transaction transaction);
}
