package org.example.post_bot_admin_user.repository;

import org.example.post_bot_admin_user.domain.Post;
import org.example.post_bot_admin_user.domain.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, String> {
    Optional<Post> findByStatusAndChatId(PostStatus status, Long chatId);
    List<Post> findAllByStatus(PostStatus status);
}
