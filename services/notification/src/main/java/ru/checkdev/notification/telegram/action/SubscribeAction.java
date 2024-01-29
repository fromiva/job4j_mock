package ru.checkdev.notification.telegram.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.domain.SubscribeTopic;
import ru.checkdev.notification.domain.TopicDTO;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.service.SubscribeTopicService;
import ru.checkdev.notification.service.UserTelegramService;
import ru.checkdev.notification.telegram.service.TgDescCallWebClint;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class SubscribeAction implements Action {
    private static final String URL_ALL_TOPICS = "/topics/";
    private final TgDescCallWebClint tgDescCallWebClint;
    private final UserTelegramService userTelegramService;
    private final SubscribeTopicService subscribeTopicService;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        String sl = System.lineSeparator();
        Long chatId = message.getChatId();
        try {
            List<TopicDTO> topics = tgDescCallWebClint.doGetList(URL_ALL_TOPICS).block();
            if (topics == null || topics.isEmpty()) {
                throw new IllegalAccessException("Empty list of topics.");
            }
            return new SendMessage(chatId.toString(), "Выбирите номер темы для подписки:" + sl + getTopicMenu(topics));
        } catch (Exception e) {
            log.error("WebClient doGetList error: {}", e.getMessage());
            return new SendMessage(chatId.toString(), "Сервис не доступен попробуйте позже" + sl + "/start");
        }
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        String sl = System.lineSeparator();
        Long chatId = message.getChatId();
        Optional<UserTelegram> optional = userTelegramService.findByTelegramChatId(chatId);
        if (optional.isEmpty()) {
            return new SendMessage(chatId.toString(), "Пользователь не найден..." + sl + "/start");
        }
        try {
            int userId = optional.get().getUserId();
            int topicId = parseTopicId(message.getText());
            if (topicId > 0) {
                SubscribeTopic subscribeTopic = new SubscribeTopic(0, userId, topicId);
                subscribeTopicService.save(subscribeTopic);
                return new SendMessage(chatId.toString(), "Вы успешно подписаны на тему: " + topicId);
            }
        } catch (Exception exception) {
            log.error("Subscribe to topic error: {}", exception.getMessage());
            return new SendMessage(chatId.toString(), "Сервис не доступен попробуйте позже" + sl + "/start");
        }
        return new SendMessage(chatId.toString(), "Ошибка подписки..." + sl + "/start");
    }

    private String getTopicMenu(List<TopicDTO> topics) {
        return topics.stream()
                .map(topic -> "/" + topic.getId()
                        + " - " + topic.getCategory().getName()
                        + " : " + topic.getName())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private int parseTopicId(String string) {
        try {
            return Integer.parseInt(string.substring(1));
        } catch (NumberFormatException exception) {
            log.error("Integer parse error: {}", exception.getMessage());
            return -1;
        }
    }
}
