package jngvarr.ru.pto_ackye_rzhd.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

import static jngvarr.ru.pto_ackye_rzhd.telegram.TBotService.FLEXIBLE_FORMATTER;
import static jngvarr.ru.pto_ackye_rzhd.telegram.TBotService.STRICT_FORMATTER;
@Component
public class DateUtils {


    /**
     * Проверяет и нормализует дату в формат dd.MM.yyyy.
     *
     * @param msgText строка с датой
     * @return нормализованная дата или null, если неверная
     */
    public String normalizeDate(String msgText) {
        if (msgText == null || msgText.trim().isEmpty()) {
            return null;
        }

        msgText = msgText.trim();

        // 1. Проверка на допустимый шаблон (дд.мм.гг или д.м.гггг)
        if (!msgText.matches("^\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}$")) {
            return null;
        }

        // 2. Разбираем строку на части
        String[] parts = msgText.split("\\.");
        if (parts.length != 3) return null;

        String day = parts[0];
        String month = parts[1];
        String year = parts[2];

        // Если год указан в виде 2 цифр → добавляем "20"
        if (year.length() == 2) {
            year = "20" + year;
        }

        String normalizedInput = day + "." + month + "." + year;

        // 3. Парсим с гибким форматом
        try {
            LocalDate date = LocalDate.parse(normalizedInput, FLEXIBLE_FORMATTER);
            // 4. Возвращаем нормализованную строку
            return date.format(STRICT_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
