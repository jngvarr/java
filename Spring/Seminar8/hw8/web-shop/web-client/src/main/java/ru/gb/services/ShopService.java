package ru.gb.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.gb.aspect.LogAction;
import ru.gb.model.Product;
import ru.gb.model.api.Payment;
import ru.gb.model.api.Storage;

import java.util.List;

/**
 * Сервис для осуществления покупки.
 */
@Service
public class ShopService {

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
        private final String shopAccount;

        /**
         * Конструктор класса.
         * @param paymentApi api оплаты.
         * @param storageApi api склада.
         * @param shopAccount номер счета магазина.
         */
        public ShopService(Payment paymentApi, Storage storageApi,String shopAccount) {
            this.paymentApi = paymentApi;
            this.storageApi = storageApi;
            this.shopAccount = shopAccount;
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
}
