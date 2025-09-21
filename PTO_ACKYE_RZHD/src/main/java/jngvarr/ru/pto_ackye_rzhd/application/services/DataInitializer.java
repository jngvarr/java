package jngvarr.ru.pto_ackye_rzhd.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ExcelFileService excelFileService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean needInitialize = true;
        if (needInitialize) { // TODO реализовать необходимость обновление данных из файла
            log.info("Приложение запущено. Инициализирую данные...");

            String filePath = "d:\\\\Downloads\\\\пто\\\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx";

            try {
                excelFileService.addDataFromExcelFile(filePath);
                log.info("Загрузка данных из Excel завершена.");
            } catch (Exception e) {
                log.error("Ошибка при загрузке данных из Excel-файла", e);
            }
        }
    }
}

