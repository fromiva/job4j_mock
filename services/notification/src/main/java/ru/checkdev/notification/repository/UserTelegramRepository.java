package ru.checkdev.notification.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.notification.domain.UserTelegram;

import java.util.Optional;

public interface UserTelegramRepository extends CrudRepository<UserTelegram, Integer> {

    @NonNull Optional<UserTelegram> findByTelegramChatId(@NonNull Long telegramChatId);


    @Transactional
    @Modifying
    @NonNull int deleteAllByTelegramChatId(@NonNull Long telegramChatId);
}
