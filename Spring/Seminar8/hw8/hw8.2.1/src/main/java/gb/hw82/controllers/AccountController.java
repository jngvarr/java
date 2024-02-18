package gb.hw82.controllers;
import gb.hw82.model.Account;
import gb.hw82.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import gb.hw82.dto.TransferRequest;

import java.util.List;

@AllArgsConstructor
@RestController
public class AccountController {

  private final PaymentService paymentService;

  @PostMapping("/transfer")
  public void transferMoney(
      @RequestBody TransferRequest request
      ) {
    paymentService.processPayment(
        request.getSenderAccountId(),
        request.getAmount());
  }

  @GetMapping("/accounts")
  public List<Account> getAllAccounts() {
    return paymentService.getAllAccounts();
  }
}
