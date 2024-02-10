package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.service.UserTelegramService;

@RequiredArgsConstructor
@Slf4j
public class UnsubscribeAction implements Action {

    private final UserTelegramService userTelegramService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        String sl = System.lineSeparator();
        Long chatId = message.getChatId();
        try {
            boolean result = userTelegramService.deleteByTelegramChatId(chatId);
            var text = result ? "Ваш Telegram успешно отвязан от аккаунта" : "Пользователь не найден.";
            return new SendMessage(chatId.toString(), text);
        } catch (Exception e) {
            log.error("Unsubscribe to topic error: {}", e.getMessage());
            return new SendMessage(chatId.toString(), "Ошибка отдписки..." + sl + "/start");
        }
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return this.handle(message);
    }
}
