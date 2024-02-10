package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

@RequiredArgsConstructor
@Slf4j
public class SubscribeAction implements Action {
    private static final String URL_PROFILES = "/profiles";
    private final TgAuthCallWebClint tgAuthCallWebClint;
    private final UserTelegramService userTelegramService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        String sl = System.lineSeparator();
        return new SendMessage(message.getChatId().toString(),
                "Введите ваши email и пароль, разделенные пробелом, например:" + sl
                        + "email@example.com abcdefgh");
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        String sl = System.lineSeparator();
        Long chatId = message.getChatId();
        var credentials = parseEmailAndPassword(message.getText());
        var person = new PersonDTO(0, null, credentials[0], credentials[1], true, null, null);
        var text = "";
        try {
            var optional = tgAuthCallWebClint.doPost(URL_PROFILES, person)
                    .onErrorComplete(WebClientResponseException.NotFound.class).blockOptional();
            if (optional.isEmpty()) {
                text = "Неверные email и/или пароль.";
                return new SendMessage(chatId.toString(), text);
            }
            userTelegramService.save(new UserTelegram(optional.get().getId(), chatId));
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            text = "Сервис не доступен попробуйте позже" + sl + "/start";
            return new SendMessage(chatId.toString(), text);
        }
        text = "Ваш Telegram успешно привязан к аккаунту";
        return new SendMessage(chatId.toString(), text);
    }

    private String[] parseEmailAndPassword(String string) {
        return string.split(" ", 2);
    }
}
