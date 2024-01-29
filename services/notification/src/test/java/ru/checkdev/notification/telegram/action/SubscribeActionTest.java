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
import ru.checkdev.notification.domain.CategoryDTO;
import ru.checkdev.notification.domain.SubscribeTopic;
import ru.checkdev.notification.domain.TopicDTO;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.service.SubscribeTopicService;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.service.TgDescCallWebClint;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SubscribeActionTest {

    @MockBean
    private TgDescCallWebClint tgDescCallWebClint;
    @MockBean
    private UserTelegramService userTelegramService;
    @MockBean
    private SubscribeTopicService subscribeTopicService;
    private SubscribeAction subscribeAction;

    private final Long chatId = 1000L;
    private final String chatType = "chat";
    private final Chat chat = new Chat(chatId, chatType);
    private final Message message = new Message();

    private final Calendar calendar = Calendar.getInstance();
    private final CategoryDTO category = new CategoryDTO(1, "Category name", 1, 1);
    private final TopicDTO topic = new TopicDTO(1, "Topic Name", "Text", calendar, calendar, 1, 1, category);

    private final Integer userId = 100;
    private final UserTelegram userTelegram = new UserTelegram(userId, chatId);

    @BeforeEach
    void beforeEach() {
        subscribeAction = new SubscribeAction(tgDescCallWebClint, userTelegramService, subscribeTopicService);
        message.setChat(chat);
    }

    @Test
    void handle() {
        List<TopicDTO> topics = List.of(topic);
        when(tgDescCallWebClint.doGetList(any())).thenReturn(Mono.just(topics));
        SendMessage actual = (SendMessage) subscribeAction.handle(message);
        assertThat(actual.getChatId()).isEqualTo(chatId.toString());
        assertThat(actual.getText()).startsWith("Выбирите номер темы для подписки");
    }

    @Test
    void callbackWhenCorrectUserChatId() {
        SubscribeTopic subscribeTopic = new SubscribeTopic(0, userId, topic.getId());
        message.setText("/" + topic.getId());
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.of(userTelegram));
        when(subscribeTopicService.save(subscribeTopic)).thenReturn(subscribeTopic);
        SendMessage actual = (SendMessage) subscribeAction.callback(message);
        assertThat(actual.getText()).startsWith("Вы успешно подписаны на тему");
    }
    @Test
    void callbackWhenIncorrectUserChatId() {
        when(userTelegramService.findByTelegramChatId(chatId)).thenReturn(Optional.empty());
        SendMessage actual = (SendMessage) subscribeAction.callback(message);
        assertThat(actual.getText()).startsWith("Пользователь не найден");
    }
}
