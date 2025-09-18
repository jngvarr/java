package jngvarr.ru.pto_ackye_rzhd.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static jngvarr.ru.pto_ackye_rzhd.telegram.PtoTelegramBotContent.ERROR_TEXT;

@Data
@Slf4j
@Component
@RequiredArgsConstructor

public class TBotMessageService {
    private static final long ADMIN_CHAT_ID = 199867696L;
    private Map<Long, Integer> sentMessagesIds = new HashMap<>();
    private List<Message> sentMessages = new ArrayList<>();
    private final BotExecutor botExecutor;

    public void sendMessage(long chatId, long userId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        executeMessage(message, chatId);
    }

    public void editMessage(long chatId, long userId, String newTextToReplace) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(sentMessagesIds.get(userId));
        editMessage.setText(newTextToReplace);

        try {
            botExecutor.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    public void forwardMessage(Message userMessage) {
        ForwardMessage forward = new ForwardMessage();
        forward.setChatId(String.valueOf(ADMIN_CHAT_ID)); // куда
        forward.setFromChatId(String.valueOf(userMessage.getChatId())); // откуда
        forward.setMessageId(userMessage.getMessageId()); // какое сообщение

        try {
            botExecutor.execute(forward);
        } catch (TelegramApiException e) {
            log.error("Не удалось переслать сообщение админу: " + e.getMessage());
        }
    }

    void executeMessage(SendMessage message, long userId) {
        try {
            Message sentMessage = botExecutor.execute(message); // Отправляем сообщение в Telegram
            sentMessagesIds.put(userId, sentMessage.getMessageId());
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    public void sendTextMessage(String text, Map<String, String> buttons, Long chatId, long userId, int columns) {
        try {
            SendMessage message = createMessage(text, buttons, chatId, columns);
            CompletableFuture<Message> future = botExecutor.executeAsync(message);
            Message sentMessage = future.get(); // если всё же нужен результат
            this.sentMessages.add(sentMessage);
            sentMessagesIds.put(userId, sentMessage.getMessageId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void editTextAndButtons(String text, Map<String, String> buttons, Long chatId, Long userId, int columns) {
        Integer messageId = sentMessagesIds.get(userId);
        if (messageId == null) {
            log.warn("Нет messageId для userId {}", userId);
            return;
        }

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        editMessage.setParseMode("markdown");

        // Создаём временный объект для формирования разметки
        if (buttons != null && !buttons.isEmpty()) {
            SendMessage temp = new SendMessage();
            attachButtons(temp, buttons, columns);
            editMessage.setReplyMarkup((InlineKeyboardMarkup) temp.getReplyMarkup());
        }

        try {
            botExecutor.execute(editMessage);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
//            log.error("Ошибка редактирования сообщения для userId {}: {}", userId, e.getMessage());
        }
    }


    public SendMessage createMessage(String text, Map<String, String> buttons, Long userId, int columns) {
        SendMessage message = createMessage(text, userId);
        if (buttons != null && !buttons.isEmpty())
            attachButtons(message, buttons, columns);
        return message;
    }


    public SendMessage createMessage(String text, Long chatId) {
        SendMessage message = new SendMessage();
        message.setText(new String(text.getBytes(), StandardCharsets.UTF_8));
        message.setParseMode("markdown");
        message.setChatId(chatId);
        return message;
    }


    private void attachButtons(SendMessage message, Map<String, String> buttons, int columns) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        Iterator<Map.Entry<String, String>> iterator = buttons.entrySet().iterator();
        while (iterator.hasNext()) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            // Добавляем до columns кнопок в текущий ряд
            for (int i = 0; i < columns && iterator.hasNext(); i++) {
                Map.Entry<String, String> entry = iterator.next();
                String buttonName = entry.getKey();
                String buttonValue = entry.getValue();

                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(new String(buttonName.getBytes(), StandardCharsets.UTF_8));
                button.setCallbackData(buttonValue);
                row.add(button); // Добавляем кнопку в ряд
            }
            keyboard.add(row);
        }
        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);
    }
}
