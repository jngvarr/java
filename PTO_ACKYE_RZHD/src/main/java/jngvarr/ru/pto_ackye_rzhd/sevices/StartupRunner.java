package jngvarr.ru.pto_ackye_rzhd.sevices;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Приложение запущено!");
    }
}

