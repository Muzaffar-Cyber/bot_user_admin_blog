package org.example.post_bot_admin_user.repository;

import org.example.post_bot_admin_user.domain.UserMessages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageRepository extends JpaRepository<UserMessages, String> {
}
