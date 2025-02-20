package jngvarr.ru.pto_ackye_rzhd.telegram;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
@Slf4j
@Component
public class FileManagement {

    private static final LocalDate TODAY = LocalDate.now();
    static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    static String formattedCurrentDate = TODAY.format(YYYY_MM_DD);

    private static final String WORKING_FOLDER = "\\" + TODAY.getYear() + "\\" + TODAY.format(DateTimeFormatter.ofPattern("LLLL", Locale.forLanguageTag("ru-RU"))).toUpperCase();
    static final String PHOTO_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\ФОТО (Подтверждение работ)\\" + WORKING_FOLDER;
    static final String PLAN_OTO_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\Контроль ПУ РРЭ (Задания на ОТО РРЭ).xlsx";
    static final String OPERATION_LOG_PATH = "d:\\YandexDisk\\ПТО РРЭ РЖД\\План ОТО\\ОЖ.xlsx";

}
