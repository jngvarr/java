package jngvarr.ru.pto_ackye_rzhd.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static jngvarr.ru.pto_ackye_rzhd.telegram.TBotService.FLEXIBLE_FORMATTER;
import static jngvarr.ru.pto_ackye_rzhd.telegram.TBotService.STRICT_FORMATTER;
@Component
public class DateUtils {

    public static final LocalDate TODAY = LocalDate.now();
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    public static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter DD_MM_YY = DateTimeFormatter.ofPattern("dd.MM.yy");
    public static final String FORMATTED_CURRENT_DATE = TODAY.format(YYYY_MM_DD);
    public static final String STRAIGHT_FORMATTED_CURRENT_DATE = TODAY.format(DD_MM_YYYY);


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
