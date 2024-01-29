package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PasswordForgetActionTest {

    @MockBean
    private TgAuthCallWebClint tgAuthCallWebClint;
    @MockBean
    private UserTelegramService userTelegramService;
    private PasswordForgetAction passwordForgetAction;

    private final Long chatId = 1000L;
    private final String chatType = "chat";
    private final Chat chat = new Chat(chatId, chatType);
    private final Message message = new Message();

    private final Integer userId = 100;
    private final UserTelegram userTelegram = new UserTelegram(userId, chatId);

    @BeforeEach
    void beforeEach() {
        passwordForgetAction = new PasswordForgetAction(tgAuthCallWebClint, userTelegramService);
        message.setChat(chat);
    }

    @Test
    void handleWhenCorrectUserId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.of(userTelegram));
        when(tgAuthCallWebClint.doGet(any())).thenReturn(Mono.empty());
        SendMessage actual = (SendMessage) passwordForgetAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).startsWith("Новый пароль выслан");
    }

    @Test
    void handleWhenIncorrectUserChatId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.empty());
        SendMessage actual = (SendMessage) passwordForgetAction.handle(message);
        assertThat(actual.getText()).startsWith("Пользователь не найден");
    }

    @Test
    void handleWhenIncorrectUserId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.of(userTelegram));
        when(tgAuthCallWebClint.doGet(any())).thenThrow(WebClientResponseException.NotFound.class);
        SendMessage actual = (SendMessage) passwordForgetAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).startsWith("Пользователь не найден");
    }

    @Test
    void callbackWhenCorrectUserId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.of(userTelegram));
        when(tgAuthCallWebClint.doGet(any())).thenReturn(Mono.empty());
        SendMessage actual = (SendMessage) passwordForgetAction.callback(message);
        assertThat(actual.getText()).isEqualTo("/start");
    }

    @Test
    void callbackWhenIncorrectUserChatId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.empty());
        SendMessage actual = (SendMessage) passwordForgetAction.callback(message);
        assertThat(actual.getText()).isEqualTo("/start");
    }

    @Test
    void callbackWhenIncorrectUserId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.of(userTelegram));
        when(tgAuthCallWebClint.doGet(any())).thenThrow(WebClientResponseException.NotFound.class);
        SendMessage actual = (SendMessage) passwordForgetAction.callback(message);
        assertThat(actual.getText()).isEqualTo("/start");
    }
}
