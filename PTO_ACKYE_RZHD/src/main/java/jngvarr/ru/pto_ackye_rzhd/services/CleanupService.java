package jngvarr.ru.pto_ackye_rzhd.services;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class CleanupService {

    @PreDestroy
    public void onShutdown() {
        System.out.println("Приложение завершает работу...");
    }
}
