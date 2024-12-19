package jngvarr.ru.pto_ackye_rzhd.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final PtoService ptoService;

    public StartupRunner(PtoService ptoService) {
        this.ptoService = ptoService;
    }

    @Override
    public void run(String... args) {
        System.out.println("Приложение запущено!");

        ptoService.processFile();
    }
}

