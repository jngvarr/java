package jngvarr.ru.pto_ackye_rzhd.telegram;


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
            Итак начнём:
            
            Прикрепите фотографию:
            """;

    public static final String OTO = """
            *ВЫБРАН РЕЖИМ "ОТО"*
            В данном режиме вам нужно выбрать вид работ ОТО и следовать дальнейшим подсказкам.
            Итак начнём:
             
            Выберите вид работ ОТО:
            """;

    public static final String NEW_TU = """
            *ДОБАВЛЕНИЕ НОВОЙ ТОЧКИ УЧЁТА*
                                    
            Последовательно вводите данные необходимые для заполнения НСИ:
            """;

    static final String HELP = "This bot is created to demonstrate Spring capabilities. \n\n" +
            "You can execute commands from the main menu or by typing a command\n\n" +
            "Type /start to see \"Welcome\" message\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see this message again\n\n";
}


