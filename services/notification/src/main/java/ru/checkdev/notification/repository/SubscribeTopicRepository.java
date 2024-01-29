package ru.checkdev.notification.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.notification.domain.SubscribeTopic;
import java.util.List;

public interface SubscribeTopicRepository extends CrudRepository<SubscribeTopic, Integer> {
    @Override
    List<SubscribeTopic> findAll();

    List<SubscribeTopic> findByUserId(int id);

    SubscribeTopic findByUserIdAndTopicId(int userId, int topicId);

    @Transactional
    @Modifying
    int deleteAllByUserIdAndTopicId(int userId, int topicId);
}