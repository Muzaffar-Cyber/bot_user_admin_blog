package org.example.post_bot_admin_user.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private Long userTelegramIdadminTelegramId;
    private Long chatId;

}
