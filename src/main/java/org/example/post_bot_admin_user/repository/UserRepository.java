package org.example.post_bot_admin_user.repository;

import org.example.post_bot_admin_user.domain.Role;
import org.example.post_bot_admin_user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserTelegramId(String telegramId);


    Optional<User> findByAdminRole(Role role);
}
