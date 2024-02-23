package ru.gb.web_client.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.gb.web_client.aspect.LogAction;
import ru.gb.web_client.clients.PaymentClientApi;
import ru.gb.web_client.clients.StorageClientApi;
import ru.gb.web_client.model.Order;
import ru.gb.web_client.model.Product;
import ru.gb.web_client.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис для осуществления покупки.
 */
@Service
@RequiredArgsConstructor
public class ShopService {


    /**
     * +
     * Api оплаты.
     */
    private final PaymentClientApi paymentApi;
    /**
     * Api склада товаров.
     */
    private final StorageClientApi storageApi;


    /**
     * Получение все товаров со склада.
     *
     * @return список товаров.
     */
    @LogAction
    public List<Product> getAll() {
        return storageApi.getProducts();
    }


    @LogAction
    public void buyProduct(Long productId, int amount, BigDecimal sum, Long numberCredit) {
        Transaction transaction = new Transaction();
        productReserve(productId, amount);
        try {
            payOrder(sum, numberCredit);
            try {
                productBuy(productId, amount);
            } catch (HttpClientErrorException e) {
                rollbackPayOrder(sum, numberCredit);
                rollbackProductReserve(productId, amount);
                throw e;
            }
        } catch (HttpClientErrorException e) {
            rollbackProductReserve(productId, amount);
            throw e;
        }
    }

    /**
     * Резервирование продукта на складе.
     *
     * @param id     идентификатор продукта.
     * @param amount количество.
     */
    @LogAction
    private void productReserve(Long id, int amount)
            throws HttpClientErrorException {
        storageApi.reserveProduct(id, Order.builder().productsAmount(amount).build());
    }

    /**
     * Служебный метод отката резервирования товара
     *
     * @param id     идентификатор товара.
     * @param amount количество.
     */
    @LogAction
    private void rollbackProductReserve(Long id, int amount)
            throws HttpClientErrorException {
        storageApi.rollbackReserve(id, Order.builder().productsAmount(amount).build());
    }

    /**
     * Оформление покупки, уменьшение остатка на складе.
     *
     * @param id     идентификатор продукта.
     * @param amount количество товара.
     */
    @LogAction
    private void productBuy(Long id, int amount)
            throws HttpClientErrorException {
        storageApi.buy(id, Order.builder().productsAmount(amount).build());
    }

    /**
     * Оплата товара
     *
     * @param sum          сумма для оплаты.
     * @param numberCredit номер счета списания.
     */
    @LogAction
    private void payOrder(BigDecimal sum, Long numberCredit)
            throws HttpClientErrorException {
        Transaction transaction = new Transaction();
        transaction.setCreditNumber(numberCredit);
        transaction.setDebitNumber(0L);
        transaction.setSum(sum);
        paymentApi.pay(transaction);
    }

    /**
     * Служебный метод отката произведенной оплаты.
     *
     * @param sum          сумма операции.
     * @param numberCredit номер счета.
     */
    @LogAction
    private void rollbackPayOrder(BigDecimal sum, Long numberCredit)
            throws HttpClientErrorException {
        Transaction transaction = new Transaction();
        transaction.setCreditNumber(numberCredit);
        transaction.setDebitNumber(0L);
        transaction.setSum(sum);
        paymentApi.rollback(transaction);
    }

}
