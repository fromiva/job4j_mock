package ru.checkdev.notification.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import ru.checkdev.notification.domain.UserTelegram;

import java.util.Optional;

public interface UserTelegramRepository extends CrudRepository<UserTelegram, Integer> {

    @NonNull Optional<UserTelegram> findByTelegramChatId(@NonNull Long chatId);
}
