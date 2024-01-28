package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Optional;

@RequiredArgsConstructor
public class WhoamiAction implements Action {

    private static final String URL_USER_PROFILES = "/profiles";
    private final TgAuthCallWebClint tgAuthCallWebClint;
    private final UserTelegramService userTelegramService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        String sl = System.lineSeparator();
        Long chatId = message.getChatId();
        Optional<UserTelegram> optional = userTelegramService.findByTelegramChatId(chatId);
        if (optional.isEmpty()) {
            return new SendMessage(chatId.toString(), "Пользователь не найден..." + sl + "/start");
        }
        Integer userId = optional.get().getUserId();
        PersonDTO result = tgAuthCallWebClint.doGet(URL_USER_PROFILES + "/" + userId).block();
        if (result != null) {
            return new SendMessage(chatId.toString(),
                    "Ваши данные: " + sl
                            + "Пользователь: " + result.getUsername() + sl
                            + "Почта: " + result.getEmail() + sl
                            + "/start");
        }
        return new SendMessage(chatId.toString(), "Пользователь не найден..." + sl + "/start");
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
