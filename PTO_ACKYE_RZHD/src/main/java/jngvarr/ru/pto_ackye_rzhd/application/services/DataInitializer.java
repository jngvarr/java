package jngvarr.ru.pto_ackye_rzhd.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final ExcelFileService excelFileService;
    private static final String FOLDER_PATH = "d:\\Downloads\\пто\\";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        boolean needInitialize = false;
        if (needInitialize) { // TODO реализовать необходимость обновление данных из файла
            log.info("Приложение запущено. Инициализирую данные...");

            File[] files = new File(FOLDER_PATH).listFiles((dir, name) -> name.endsWith(".xlsx"));
            if (files == null || files.length == 0) {
                log.info("No files found in folder: " + FOLDER_PATH);
                return;
            }

//            String filePath = "d:\\\\Downloads\\\\пто\\\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx";

            for (File file : files) {
                if (file.getName().startsWith("Состав ИИК")) {
                    try {
//                excelFileService.addDataFromExcelFile(filePath);
                        String path = file.getPath();
                        long startTime = System.currentTimeMillis();
                        excelFileService.addDataFromIikContent(path);
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("Execution time: " + duration / 1000 + " seconds");
                        log.info("Загрузка данных из Excel завершена.");
                    } catch (Exception e) {
                        log.error("Ошибка при загрузке данных из Excel-файла", e);
                    }
                }
            }
        }
    }
}

