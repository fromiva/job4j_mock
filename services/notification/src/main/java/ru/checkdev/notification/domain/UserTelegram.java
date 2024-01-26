package ru.checkdev.notification.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "cd_user_telegram")
public class UserTelegram {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "user_id")
    Integer userId;

    @Column(name = "telegram_chat_id")
    Long telegramChatId;
}
