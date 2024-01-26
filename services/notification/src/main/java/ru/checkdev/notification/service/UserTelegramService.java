package ru.checkdev.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.checkdev.notification.domain.UserTelegram;
import ru.checkdev.notification.repository.UserTelegramRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserTelegramService {

    private final UserTelegramRepository repository;

    public UserTelegram save(@NonNull UserTelegram entity) {
        return repository.save(entity);
    }

    public Optional<UserTelegram> findByUserId(@NonNull Integer userId) {
        return repository.findById(userId);
    }

    public Optional<UserTelegram> findByTelegramChatId(@NonNull Long chatId) {
        return repository.findByTelegramChatId(chatId);
    }

    public void deleteByUserId(@NonNull Integer userId) {
        repository.deleteById(userId);
    }
}
