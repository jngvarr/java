package jngvarr.ru.pto_ackye_rzhd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final PtoService ptoService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Приложение запущено. Инициализирую данные...");

        String filePath = "d:\\\\Downloads\\\\пто\\\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx";

        try {
            ptoService.addDataFromExcelFile(filePath);
            log.info("Загрузка данных из Excel завершена.");
        } catch (Exception e) {
            log.error("Ошибка при загрузке данных из Excel-файла", e);
        }
    }
}

