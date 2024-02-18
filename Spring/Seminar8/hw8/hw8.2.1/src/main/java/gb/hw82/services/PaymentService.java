package gb.hw82.services;

import gb.hw82.aspect.LogAction;
import gb.hw82.aspect.MeasureTime;
import gb.hw82.model.Account;
import gb.hw82.model.Product;
import gb.hw82.repository.AccountRepository;
import gb.hw82.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {


    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final AccountService accountService;

    @Transactional
    @LogAction(description = "Processing payment for the product")
    @MeasureTime
    public void processPayment(Long productId, Long accountId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Account buyerAccount = accountService.getUserAccount(accountId);
        Account shopAccount = accountService.getShopAccount();
        if (buyerAccount.getBalance() >= product.getPrice()) {
            buyerAccount.setBalance(buyerAccount.getBalance() - product.getPrice());
            accountRepository.save(buyerAccount);
            shopAccount.setBalance(shopAccount.getBalance() + product.getQuantity());
            accountRepository.save(shopAccount);
        } else {
            throw new RuntimeException("Insufficient funds");
        }
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}

