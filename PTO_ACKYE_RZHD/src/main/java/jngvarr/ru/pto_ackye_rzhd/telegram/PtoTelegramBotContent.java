package jngvarr.ru.pto_ackye_rzhd.telegram;


import java.util.List;
import java.util.Map;

public class PtoTelegramBotContent {
    public static final String MAIN_MENU = """
            *Beta версия телеграм бота призванного помочь облегчить участь персонала ООО "УК СТС"*
                                                                 
            Бот умеет делать следующие вещи:                                                        \s
                1. Обрабатывать / сортировать фотоматериалы ПТО
                2. Обрабатывать / сортировать фотоматериалы ОТО
                3. Что-то еще, пока не придумал
                Выбери вид работ:
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

    static final String HELP = "This bot was created to facilitate data processing during\n " +
            "maintenance of the commercial electricity metering system\n " +
            "of the West Siberian Railway of Russian Railways by the\n" +
            "personnel of CPC Management Company LLC CC CTC\n\n" +
            "You can execute commands from the main menu or by typing a command\n\n" +
            "Type /start to see \"Welcome\" message\n\n" +
//            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see this message again\n\n";

    static final Map<TBot.OtoType, Map<Integer, String>> replacedEquipmentDatum = Map.of(
            TBot.OtoType.TT_CHANGE, Map.of(
                    0, "Введите тип трансформаторов тока (пример: ТШП-0,66): ",
                    1, "Введите коэффициент трансформации (пример: 300/5): ",
                    2, "Введите класс точности (пример: 0,5 или 0,5S): ",
                    3, "Введите год выпуска трансформаторов (пример: 2025): ",
                    4, "Введите номер ТТ ф.A: ",
                    5, "Введите номер ТТ ф.B: ",
                    6, "Введите номер ТТ ф.C: ",
                    7, "Опишите причину замены: "),
            TBot.OtoType.METER_CHANGE, Map.of(
                    0, "Введите показания демонтируемого прибора учета:",
                    1, "Введите номер устанавливаемого прибора учета:",
                    2, "Введите показания устанавливаемого прибора учета:",
                    3, "Опишите причину замены: "),
            TBot.OtoType.DC_CHANGE, Map.of(
                    0, "Введите номер устанавливаемого концентратора.",
                    1, "Опишите причину замены: ")
    );

    static final Map<TBot.UserState, Map<Integer, String>> mountedEquipmentDatum = Map.of(
            TBot.UserState.IIK_MOUNT, Map.of(
                    0, "Введите тип прибора учета: ",
                    1, "Введите наименование станции: ",
                    2, "Введите наименование подстанции: ",
                    3, "Введите наименование точки учета: ",
                    4, "Введите адрес точки учета: ",
                    5, "Введите место установки прибора учета: ",
                    6, "Введите наименование монтажной организации ",
                    7, "Введите ФИО монтажника ",
                    8, "Введите дату монтажа "),
            TBot.UserState.DC_MOUNT, Map.of(
                    0, "Введите наименование подстанции: ",
                    1, "Введите наименование станции: ",
                    2, "Введите наименование монтажной организации ",
                    3, "Введите ФИО монтажника ",
                    4, "Введите дату монтажа ")
    );

    static final String[] dcColumnsToClear = {
            "Присоединение",
            "Точка учёта",
            "Место установки счетчика (Размещение счетчика)",
            "Адрес установки",
            "Марка счётчика",
            "Номер счетчика"};

    static final String[] not123ColumnsToClear = {
            "Марка счётчика",
            "Номер счетчика",
            "Номер УСПД",
            "Статус счетчика в Горизонте на",
            "Счетчик в Горизонте отмечен как НОТ?",
            "ВСЕГО счетчиков на УСПД",
            "Задание на ОТО от диспетчера"};
    static final Map<String, List<String>> fillingData = Map.of(
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
            "addIIK", List.of("Монтаж новой ТУ\n", "Монтаж новой ТУ.", "Монтаж новой ТУ. "),
            "dcMount", List.of("Монтаж нового концентратора. \n", "Монтаж нового концентратора.", " Монтаж нового концентратора.")
    );

    static final Map<String, String> eelToNtel = Map.of(
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

    static final Map<String, String> startMenuButtons = Map.of(
            "ПТО", "pto",
            "ОТО", "oto",
            "Монтаж новой ТУ / концентратора", "newTU"
    );

    static final Map<String, Map<String, String>> modes = Map.of(
            "pto", Map.of(
                    "Добавление фото счетчика", "ptoIIK",
                    "Добавление фото ИВКЭ", "ptoIVKE"),
            "oto", Map.of(
                    "ОТО ИИК", "otoIIK",
                    "ОТО ИВКЭ", "otoIVKE"),
            "newTU", Map.of(
                    "Монтаж новой точки учёта (ИИК)", "addIIK",
                    "Монтаж нового концентратора (ИВКЭ)", "dcMount")
    );

    static final Map<String, String> otoIIKButtons = Map.of(
            "Сброшена ошибка ключа (WK)", "wkDrop",
            "Замена счетчика", "meterChange",
            "Замена трансформаторов тока", "ttChange",
            "Восстановление питания ТУ", "powerSupplyRestoring",
            "Присвоение статуса НОТ", "setNot");

    static final Map<String, String> otoIVKEButtons = Map.of(
            "Замена концентратора", "dcChange",
            "Перезагрузка концентратора", "dcRestart",
            "Концентратор отключен (без питания)", "setNot",
            "Восстановление питания", "powerSupplyRestoring");

    static final Map<String, String> confirmMenu = Map.of(
            "Подтвердить выполнение", "confirm",
            "Отменить выполнение", "cancel");

    static final Map<String, String> CompleteButton = Map.of(
            "Завершить загрузку данных", "LOADING_COMPLETE");
}


