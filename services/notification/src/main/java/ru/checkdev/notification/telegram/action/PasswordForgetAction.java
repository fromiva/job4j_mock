package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class PasswordForgetAction implements Action {

    private static final String URL_FORGOT = "/v2/forgot";
    private final TgAuthCallWebClint tgAuthCallWebClint;
    private final UserTelegramService userTelegramService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        String sl = System.lineSeparator();
        String text = "Пользователь не найден..." + sl + "/start";
        Long chatId = message.getChatId();
        Optional<UserTelegram> optional = userTelegramService.findByTelegramChatId(chatId);
        if (optional.isEmpty()) {
            return new SendMessage(chatId.toString(), text);
        }
        try {
            Integer userId = optional.get().getUserId();
            tgAuthCallWebClint.doGet(URL_FORGOT + "/" + userId).block();
            text = "Новый пароль выслан вам на почту, указанную при регистрации...";
            return new SendMessage(chatId.toString(), text);
        } catch (WebClientResponseException.NotFound e) {
            return new SendMessage(chatId.toString(), text);
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl + "/start";
            return new SendMessage(chatId.toString(), text);
        }
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return new SendMessage(message.getChatId().toString(), "/start");
    }
}
