package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.domain.PersonDTO;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClint;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class WhoamiActionTest {

    @MockBean
    private TgAuthCallWebClint tgAuthCallWebClint;
    @MockBean
    private UserTelegramService userTelegramService;
    private WhoamiAction whoamiAction;


    Long chatId = 1000L;
    String chatType = "chat";
    Chat chat = new Chat(chatId, chatType);
    Message message = new Message();

    Integer userId = 100;
    String userName = "username";
    String userEmail = "email@example.com";
    String userPassword = "password";
    boolean userPrivacy = true;
    UserTelegram userTelegram = new UserTelegram(userId, chatId);
    PersonDTO person = new PersonDTO(userId, userName, userEmail, userPassword,
            userPrivacy, List.of(), Calendar.getInstance());


    @BeforeEach
    void beforeEach() {
        whoamiAction = new WhoamiAction(tgAuthCallWebClint, userTelegramService);
        message.setChat(chat);
    }

    @Test
    void handleWhenCorrectUserId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.of(userTelegram));
        when(tgAuthCallWebClint.doGet(any())).thenReturn(Mono.just(person));
        SendMessage actual = (SendMessage) whoamiAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText())
                .startsWith("Ваши данные:")
                .contains(userName, userEmail)
                .doesNotContain(userPassword);
    }

    @Test
    void handleWhenIncorrectUserChatId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.empty());
        SendMessage actual = (SendMessage) whoamiAction.handle(message);
        assertThat(actual.getText()).startsWith("Пользователь не найден");
    }

    @Test
    void handleWhenIncorrectUserId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.of(userTelegram));
        when(tgAuthCallWebClint.doGet(any())).thenReturn(Mono.empty());
        SendMessage actual = (SendMessage) whoamiAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).startsWith("Пользователь не найден");
    }

    @Test
    void callbackWhenCorrectUserId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.of(userTelegram));
        when(tgAuthCallWebClint.doGet(any())).thenReturn(Mono.just(person));
        SendMessage actual = (SendMessage) whoamiAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText())
                .startsWith("Ваши данные:")
                .contains(userName, userEmail)
                .doesNotContain(userPassword);
    }

    @Test
    void callbackWhenIncorrectUserChatId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.empty());
        SendMessage actual = (SendMessage) whoamiAction.callback(message);
        assertThat(actual.getText()).startsWith("Пользователь не найден");
    }

    @Test
    void callbackWhenIncorrectUserId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.of(userTelegram));
        when(tgAuthCallWebClint.doGet(any())).thenReturn(Mono.empty());
        SendMessage actual = (SendMessage) whoamiAction.callback(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).startsWith("Пользователь не найден");
    }
}
