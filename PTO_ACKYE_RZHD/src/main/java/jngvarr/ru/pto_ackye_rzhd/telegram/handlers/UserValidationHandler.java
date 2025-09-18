package jngvarr.ru.pto_ackye_rzhd.telegram.handlers;

import jngvarr.ru.pto_ackye_rzhd.domain.services.UserService;
import jngvarr.ru.pto_ackye_rzhd.telegram.UpdateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
@RequiredArgsConstructor
public class UserValidationHandler implements UpdateHandler {

    private final UserService userService;
    private final TBotMessageService messageService;

    @Override
    public boolean canHandle(Update update) {
        return true; // обрабатывает все апдейты первым
    }

    @Override
    public void handle(Update update) {
            messageService.sendMessage(update.getMessage().getChatId(), "Вы не зарегистрированы");
            throw new StopProcessingException(); // кастомное исключение, чтобы dispatcher прервал цикл



            if (user.isAccepted()) {
                if (update.hasMessage()) {
                    if (update.getMessage().hasText()) {
                        textMessageHandler.handleTextMessage(update);
                    } else if (update.getMessage().hasPhoto()) {
                        photoMessageHandler.handlePhotoMessage(update);
                    }
                } else if (update.hasCallbackQuery()) {
                    callbackQueryHandler.handleCallbackQuery(update, user);
        }
    }
}
