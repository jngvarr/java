package ru.gb.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.gb.aspect.LogAction;
import ru.gb.model.Order;
import ru.gb.model.Product;
import ru.gb.model.Transaction;
import ru.gb.model.api.Payment;
import ru.gb.model.api.Storage;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис для осуществления покупки.
 */
@Service
public class ShopService {


    private final RestTemplate template = new RestTemplate();
        /**+
         * Api оплаты.
         */
        private final Payment paymentApi;
        /**
         * Api склада товаров.
         */
        private final Storage storageApi;
        /**
         * Номер счета магазина.
         */

        /**
         * Конструктор класса.
         * @param paymentApi api оплаты.
         * @param storageApi api склада.
         */
        public ShopService(Payment paymentApi, Storage storageApi) {
            this.paymentApi = paymentApi;
            this.storageApi = storageApi;

        }

        /**
         * Получение все товаров со склада.
         * @return список товаров.
         */
        @LogAction
        public List<Product> getAll(){
            RestTemplate template = new RestTemplate();
            ResponseEntity<List<Product>> response = template.exchange(storageApi.getBaseUri(),
                    HttpMethod.GET, null, new ParameterizedTypeReference<>(){});
            return response.getBody();
        }
    /**
     * Метод покупки товара. На каждом этапе происходит проверка,
     * в случае получения исключения происходит откат выполненных транзакций.
     * @param productId идентификатор продукта.
     * @param amount количество заказанного продукта.
     * @param sum сумма заказа.
     * @param numberCredit номер счета для списания.
     */
    @LogAction
    public void buyProduct(Long productId, int amount, BigDecimal sum, Long numberCredit){
        Transaction transaction = new Transaction();
        productReserve(productId, amount);
        try {
            payOrder(sum, numberCredit);
            try{
                productBay(productId, amount);
            } catch (HttpClientErrorException e){
                rollbackPayOrder(sum, numberCredit);
                rollbackProductReserve(productId, amount);
                throw e;
            }
        }catch (HttpClientErrorException e){
            rollbackProductReserve(productId, amount);
            throw e;
        }
    }

    /**
     * Резервирование продукта на складе.
     * @param id идентификатор продукта.
     * @param amount количество.
     */
    @LogAction
    private void productReserve(Long id, int amount)
            throws HttpClientErrorException {
        String path = storageApi.getBaseUri() + id + "/reserve";
        Order order = new Order();
        order.setProductsAmount(amount);
        template.postForEntity(path, order, Object.class);
    }

    /**
     * Служебный метод отката резервирования товара
     * @param id идентификатор товара.
     * @param amount количество.
     */
    @LogAction
    private void rollbackProductReserve(Long id, int amount)
            throws HttpClientErrorException {
        String path = storageApi.getBaseUri() + id + "/reserve/rollback";
        Order order = new Order();
        order.setProductsAmount(amount);
        template.postForEntity(path, order, Object.class);
    }

    /**
     * Оформление покупки, уменьшение остатка на складе.
     * @param id идентификатор продукта.
     * @param amount количество товара.
     */
    @LogAction
    private void productBay(Long id, int amount)
            throws HttpClientErrorException {
        Order order = new Order();
        order.setProductsAmount(amount);
        template.postForEntity(storageApi.getBaseUri() + id,
                order, Object.class);
    }

    /**
     * Оплата товара
     * @param sum сумма для оплаты.
     * @param numberCredit номер счета списания.
     */
    @LogAction
    private void payOrder(BigDecimal sum, Long numberCredit)
            throws HttpClientErrorException {
        Transaction transaction = new Transaction();
        transaction.setCreditNumber(numberCredit);
        transaction.setDebitNumber(Long.parseLong("0"));
        transaction.setSum(sum);
        template.postForEntity(paymentApi.getBaseUri(),
                transaction, Object.class);
    }

    /**
     * Служебный метод отката произведенной оплаты.
     * @param sum сумма операции.
     * @param numberCredit номер счета.
     */
    @LogAction
    private void rollbackPayOrder(BigDecimal sum, Long numberCredit)
            throws HttpClientErrorException {
        Transaction transaction = new Transaction();
        transaction.setCreditNumber(numberCredit);
        transaction.setDebitNumber(Long.parseLong("0"));
        transaction.setSum(sum);
        template.postForEntity(paymentApi.getBaseUri() + "/rollback",
                transaction, Object.class);
    }

}
