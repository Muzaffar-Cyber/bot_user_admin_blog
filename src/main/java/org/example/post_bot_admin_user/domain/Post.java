package org.example.post_bot_admin_user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private Long chatId;
    @Enumerated(EnumType.STRING)
    private PostStatus status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long userId;
}
