package ru.checkdev.notification.client;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.checkdev.notification.domain.SubscribeCategory;
import ru.checkdev.notification.domain.SubscribeTopic;
import ru.checkdev.notification.service.SubscribeCategoryService;
import ru.checkdev.notification.service.SubscribeTopicService;

@RequiredArgsConstructor
@Component
public class KafkaConsumerClient {

    private final SubscribeCategoryService subscribeCategoryService;
    private final SubscribeTopicService subscribeTopicService;

    @KafkaListener(topics = "jod4j-mock-notification-subscribe-category")
    public void addCategorySubscriptionListener(SubscribeCategory subscribeCategory) {
        subscribeCategoryService.save(subscribeCategory);
    }

    @KafkaListener(topics = "jod4j-mock-notification-unsubscribe-category")
    public void deleteCategorySubscriptionListener(SubscribeCategory subscribeCategory) {
        subscribeCategoryService.delete(subscribeCategory);
    }

    @KafkaListener(topics = "jod4j-mock-notification-subscribe-topic")
    public void addTopicSubscriptionListener(SubscribeTopic subscribeTopic) {
        subscribeTopicService.save(subscribeTopic);
    }

    @KafkaListener(topics = "jod4j-mock-notification-unsubscribe-topic")
    public void deleteTopicSubscriptionListener(SubscribeTopic subscribeTopic) {
        subscribeTopicService.delete(subscribeTopic);
    }
}
