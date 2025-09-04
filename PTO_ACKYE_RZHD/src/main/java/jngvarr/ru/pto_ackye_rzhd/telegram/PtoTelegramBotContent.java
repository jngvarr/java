package jngvarr.ru.pto_ackye_rzhd.telegram;


import jngvarr.ru.pto_ackye_rzhd.telegram.domain.OtoType;
import jngvarr.ru.pto_ackye_rzhd.telegram.domain.ProcessState;

import java.util.List;
import java.util.Map;

public class PtoTelegramBotContent {
    static final String ERROR_TEXT = "Error occurred: ";

    public static final String MAIN_MENU = """
                Выбери вид работ:
            """;

    public static final String INTRO = """
            *Beta версия телеграм бота призванного помочь облегчить участь персонала ООО "УК СТС"*
                                                                 
            Бот умеет делать следующие вещи:                                                        \s
                1. Обрабатывать / сортировать фотоматериалы ПТО
                2. Обрабатывать / сортировать фотоматериалы ОТО
                3. Подготавливать информацию для внесения в ПО Горизонт
                4. Что-то еще, пока не придумал
               
            Для начала работы нажмите /start
            """;

    public static final String PTO = """
            *ВЫБРАН РЕЖИМ "ПТО"*
            В данном режиме вам нужно просто загружать фотографии ИИК или ИВКЭ.
            Выберите тип оборудования:
                        """;

    public static final String OTO = """
            *ВЫБРАН РЕЖИМ "ОТО"*
            В данном режиме вам нужно выбрать вид работ ОТО и следовать дальнейшим подсказкам.
            Выберите вид работ ОТО:
             """;

    public static final String NEW_TU = """
            *Монтажные/демонтажные работы: *\s
            """;

    public static String HELP = "This bot was created to facilitate data processing during\n " +
            "maintenance of the commercial electricity metering system\n " +
            "of the West Siberian Railway of Russian Railways by the\n" +
            "personnel of CPC Management Company LLC CC CTC\n\n" +
            "You can execute commands from the main menu or by typing a command\n\n" +
            "Type /start to see \"Welcome\" message\n\n" +
//            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see this message again\n\n";

    public static final Map<OtoType, Map<Integer, String>> REPLACED_EQUIPMENT_DATUM = Map.of(
            OtoType.TT_CHANGE, Map.of(
                    0, "Введите тип трансформаторов тока (пример: ТШП-0,66): ",
                    1, "Введите коэффициент трансформации (пример: 300/5): ",
                    2, "Введите класс точности (пример: 0,5 или 0,5S): ",
                    3, "Введите год выпуска трансформаторов (пример: 2025): ",
                    4, "Введите номер ТТ ф.A: ",
                    5, "Введите номер ТТ ф.B: ",
                    6, "Введите номер ТТ ф.C: ",
                    7, "Опишите причину замены: "),
            OtoType.METER_CHANGE, Map.of(
                    0, "Введите показания демонтируемого прибора учета:",
                    1, "Введите номер устанавливаемого прибора учета:",
                    2, "Введите показания устанавливаемого прибора учета:",
                    3, "Опишите причину замены: "),
            OtoType.DC_CHANGE, Map.of(
                    0, "Введите номер устанавливаемого концентратора.",
                    1, "Опишите причину замены: ")
    );

    static final Map<ProcessState, Map<Integer, String>> mountedEquipmentDatum = Map.of(
            ProcessState.IIK_MOUNT, Map.of(
                    0, "Введите наименование станции: ",
                    1, "Введите наименование подстанции: ",
                    2, "Введите номер прибора учёта: ",
                    3, "Введите тип прибора учета: ",
                    4, "Введите наименование точки учета: ",
                    5, "Введите адрес точки учета: ",
                    6, "Введите место установки прибора учета: ",
                    7, "Введите наименование монтажной организации: ",
                    8, "Введите ФИО монтажника: ",
                    9, "Введите дату монтажа (в формате: дд.мм.гггг): "),
            ProcessState.DC_MOUNT, Map.of(
                    0, "Введите наименование подстанции: ",
                    1, "Введите номер концентратора: ",
                    2, "Введите наименование монтажной организации: ",
                    3, "Введите ФИО монтажника: ",
                    4, "Введите дату монтажа: ")
    );

    public static final String[] DC_COLUMNS_TO_CLEAR = {
            "Присоединение",
            "Точка учёта",
            "Место установки счетчика (Размещение счетчика)",
            "Адрес установки",
            "Марка счётчика",
            "Номер счетчика"};

    public static final String[] NOT_123_COLUMNS_TO_CLEAR = {
            "Марка счётчика",
            "Номер счетчика",
            "Номер УСПД",
            "Статус счетчика в Горизонте на",
            "Счетчик в Горизонте отмечен как НОТ?",
            "ВСЕГО счетчиков на УСПД",
            "Задание на ОТО от диспетчера"};

    public static final String[] METER_MOUNT_COLUMNS_TO_CLEAR = {
            "ID",
            "Счетчик в Горизонте отмечен как НОТ?",
            "ВСЕГО счетчиков на УСПД",
            "Статус счетчика в Горизонте на",
            "Задание на ОТО от диспетчера"};
    public static final Map<String, List<String>> STRINGS_BY_ACTION_TYPE = Map.of(
            "WK", List.of("Нет связи со счетчиком",
                    "Ошибка ключа - WrongKey (сделана прошивка счетчика)",
                    " Сброшена ошибка ключа WrongKey (счетчик не на связи). "),
            "NOT", List.of("Нет связи со счетчиком",
                    "Уточнение реквизитов ТУ (подана заявка на корректировку НСИ)", " "),
            "meterSupply", List.of("Нет связи со счетчиком", "Восстановление схемы.", " Восстановление схемы подключения. "),
            "meterChange", List.of("Нет связи со счетчиком", "Неисправность счетчика (счетчик заменен)", " Замена прибора учета №"),
            "ttChange", List.of("Повреждение ТТ\n", "Повреждение ТТ (ТТ заменили)",
                    " Замена трансформаторов тока. Установлены трансформаторы "),
            "dcChange", List.of("Нет связи со всеми счетчиками\n", "Повреждение концентратора (Концентратор заменён)",
                    " Замена концентратора №"),
            "dcRestart", List.of("Нет связи со всеми счетчиками\n", "Сбой ПО устройства (сделан рестарт по питанию)",
                    " Перезагрузка концентратора. "),
            "dcSupply", List.of("Нет связи со всеми счетчиками\n", "Восстановление схемы.", " Восстановление схемы подключения."),
            "iikMount", List.of("Монтаж новой ТУ\n", "Монтаж новой ТУ.", "Монтаж новой ТУ. "),
            "dcMount", List.of("Монтаж нового концентратора. \n", "Монтаж нового концентратора.", " Монтаж нового концентратора.")
    );

    public static final Map<String, String> EEL_TO_NTEL = Map.of(
            "ЭЭЛ-1", "НТЭЛ-1",
            "НЭЭЛ-1", "НТЭЛ-1.1",
            "ЭЭЛ-2", "НТЭЛ-2",
            "ЭЭЛ-2.1", "НТЭЛ-2.1",
            "ЭЭЛ-3", "НТЭЛ-3",
            "ЭЭЛ-3.1", "НТЭЛ-3.1",
            "ЭЭЛ-3.2", "НТЭЛ-3.2",
            "ЭЭЛ-3.3", "НТЭЛ-3.3",
            "ЭЭЛ-4", "НТЭЛ-4"
    );

    public static final Map<String, String> DISCONNECT_REASON = Map.of(
            "Потребитель отключен.", "NOT",
            "Сезонный потребитель.", "seasonNOT",
            "Низкий уровень PLC сигнала", "lowPLC",
            "Прибор учета демонтирован (НОТ3)", "NOT3",
            "Прибор учета сгорел (НОТ2)", "NOT2",
            "Местонахождения ПУ неизвестно (НОТ1 украден?)", "NOT1");

    public static final Map<String, String> START_MENU_BUTTONS = Map.of(
            "ПТО", "pto",
            "ОТО", "oto",
            "Монтаж новой ТУ / концентратора", "mount"
    );

    public static final Map<String, Map<String, String>> MODES = Map.of(
            "pto", Map.of(
                    "Добавление фото счетчика", "ptoIIK",
                    "Добавление фото ИВКЭ", "ptoIVKE"),
            "oto", Map.of(
                    "ОТО ИИК", "otoIIK",
                    "ОТО ИВКЭ", "otoIVKE"),
            "mount", Map.of(
                    "Монтаж новой точки учёта (ИИК)", "iikMount",
                    "Монтаж нового концентратора (ИВКЭ)", "dcMount")
    );

    public static final Map<String, String> OTO_IIK_BUTTONS = Map.of(
            "Сброшена ошибка ключа (WK)", "wkDrop",
            "Замена счетчика", "meterChange",
            "Замена трансформаторов тока", "ttChange",
            "Восстановление питания ТУ", "powerSupplyRestoring",
            "Присвоение статуса НОТ", "setNot");

    public static final Map<String, String> OTO_IVKE_BUTTONS = Map.of(
            "Замена концентратора", "dcChange",
            "Перезагрузка концентратора", "dcRestart",
            "Концентратор отключен (без питания)", "setNot",
            "Восстановление питания", "powerSupplyRestoring");

    public static final Map<String, String> CONFIRM_MENU = Map.of(
            "Подтвердить выполнение", "confirm",
            "Отменить выполнение", "cancel");

    public static final Map<String, String> COMPLETE_BUTTON = Map.of(
            "Завершить загрузку данных", "LOADING_COMPLETE");

    public static final Map<OtoType, String> PHOTO_SUBDIRS_NAME = Map.of(
            OtoType.METER_CHANGE, "Замена ПУ",
            OtoType.TT_CHANGE, "Замена ТТ",
            OtoType.DC_CHANGE, "Замена концентратора"
    );

    public static final Map<String, String> GET_STRING_NOT = Map.of(
            "NOT", "НОТ. Потребитель отключен.",
            "seasonNOT", "НОТ. Сезонный потребитель.",
            "lowPLC", "НОТ. Низкий уровень PLC сигнала.",
            "NOT3", "Прибор учета демонтирован (НОТ3).",
            "NOT2", "Прибор учета сгорел (НОТ2).",
            "NOT1", "Местонахождения ПУ неизвестно (НОТ1)."
    );
}


