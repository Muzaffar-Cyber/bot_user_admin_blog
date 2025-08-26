package org.example.post_bot_admin_user.telegram_bot;

import org.example.post_bot_admin_user.domain.*;
import io.github.natanimn.BotContext;
import io.github.natanimn.types.updates.CallbackQuery;
import io.github.natanimn.types.updates.Message;
import lombok.RequiredArgsConstructor;
import org.example.post_bot_admin_user.repository.UserMessageRepository;
import org.example.post_bot_admin_user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.example.post_bot_admin_user.repository.PostRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BotListener {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserMessageRepository userMessageRepository;

    public void onStart(BotContext context, Message message) {
        Optional<User> byUserTelegramId = userRepository.findByUserTelegramId(message.from.id.toString());
        if (byUserTelegramId.isEmpty()) {
            User user = new User();
            user.setUsername(message.from.username);
            user.setUserTelegramId(message.from.id.toString());
            user.setFirstName(message.from.first_name);
            user.setLastName(message.from.last_name);
            user.setChatId(message.chat.id + "");
            user.setRole(Role.USER);
            user.setUserStatus(UserStatus.SELECTING_ROLE);
            user.setAdminRole(Role.USER);
            userRepository.save(user);
        } else {
            byUserTelegramId.get().setUserStatus(UserStatus.SELECTING_ROLE);
            userRepository.save(byUserTelegramId.get());
        }
        BotMenu.selectRoll(context, message.chat.id);
    }

    public void onSelectRoll(BotContext context, Message message) {
        BotMenu.blogAdminMenu(context, message.chat.id);
    }

    public void onEnterPassword(BotContext context, CallbackQuery callbackQuery) {
        Optional<User> byTelegramId = userRepository.findByUserTelegramId(callbackQuery.from.id.toString());
        if (byTelegramId.isPresent()) {
            if (byTelegramId.get().getUserStatus().equals(UserStatus.SELECTING_ROLE)) {
                context.sendMessage(callbackQuery.message.chat.id, "Enter admin password (3 chance!):").exec();
                byTelegramId.get().setUserStatus(UserStatus.PASSWORD_ENTERING);
                userRepository.save(byTelegramId.get());
            }
            if (byTelegramId.get().getUserStatus().equals(UserStatus.PASSWORD_SUCCESS)) {
                context.sendMessage(callbackQuery.message.chat.id, "Password is entered").exec();
                BotMenu.blogAdminMenu(context, callbackQuery.message.chat.id);
            }
        }
    }

    public void onEnterUser(BotContext context, CallbackQuery callbackQuery) {
        Optional<User> byUserTelegramId = userRepository.findByUserTelegramId(callbackQuery.from.id.toString());
        byUserTelegramId.ifPresent(user -> context.sendMessage(callbackQuery.message.chat.id, "Welcome " + user.getFirstName() + " " + user.getLastName()).exec());
        BotMenu.blogUserMenu(context, callbackQuery.message.chat.id);
    }

    public void onCreatePost(BotContext context, CallbackQuery callbackQuery) {
        // Logic for creating a post
        if (postRepository.findByStatusAndChatId(PostStatus.NEW, callbackQuery.message.chat.id).isEmpty()) {
            Post post = new Post();
            post.setId(UUID.randomUUID().toString());
            post.setChatId(callbackQuery.message.chat.id);
            post.setStatus(PostStatus.NEW);
            postRepository.save(post);
        }
        context.sendMessage(callbackQuery.message.chat.id, "Creating a post...\n Enter title:").exec();
    }

    public void sendMessageToAdmin(BotContext context, CallbackQuery callbackQuery) {
        Optional<User> byUserTelegramId = userRepository.findByUserTelegramId(callbackQuery.from.id.toString());
        if (byUserTelegramId.isPresent()) {
            byUserTelegramId.get().setUserStatus(UserStatus.TEXT_ENTERING);
            userRepository.save(byUserTelegramId.get());
        }
        context.sendMessage(callbackQuery.message.chat.id, "Enter message to Admin:").exec();
    }

    public void onEnterPostData(BotContext context, Message message) {
        Optional<Post> postOptional = postRepository.findByStatusAndChatId(PostStatus.NEW, message.chat.id);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            if (post.getTitle() == null) {
                post.setTitle(message.text);
                postRepository.save(post);
                context.sendMessage(message.chat.id, "Enter content:").exec();
            } else if (post.getContent() == null) {
                post.setContent(message.text);
                post.setStatus(PostStatus.PUBLISHED);
                postRepository.save(post);
                StringBuilder text = new StringBuilder("Post created successfully!");
                text.append("\nTitle: ").append(post.getTitle());
                text.append("\nContent: ").append(post.getContent());
                context.sendMessage(message.chat.id, text.toString()).exec();
                BotMenu.blogAdminMenu(context, message.chat.id);
            }
        }
    }


    public void onHandleTextData(BotContext context, Message message) {
        Optional<User> byTelegramId = userRepository.findByUserTelegramId(message.from.id.toString());
        if (byTelegramId.isPresent()) {
            User user1 = byTelegramId.get();
            if (user1.getAdminRole().equals(Role.ADMIN)) {
                if (user1.getUserStatus().equals(UserStatus.PASSWORD_ENTERING) &&
                        message.text.trim().equals("123")) {
                    user1.setUserStatus(UserStatus.PASSWORD_SUCCESS);
                    user1.setPswCounter(0);
//                    user1.setAdminRole(Role.ADMIN);
                    userRepository.save(user1);
                    context.sendMessage(message.chat.id, "Welcome ADMIN").exec();
                    BotMenu.blogAdminMenu(context, message.chat.id);
                }
                if (user1.getUserStatus().equals(UserStatus.PASSWORD_ENTERING) &&
                        !message.text.trim().equals("123")) {
                    if (user1.getPswCounter() >= 3) {
                        context.sendMessage(message.chat.id, "Password wrong. Please select the correct role again").exec();
                        BotMenu.selectRoll(context, message.chat.id);
                        user1.setPswCounter(0);
                        userRepository.save(user1);
                        return;
                    }
                    user1.setPswCounter(user1.getPswCounter() + 1);
                    userRepository.save(user1);
                    if (user1.getPswCounter() < 3) {
                        context.sendMessage(message.chat.id, String.format("Password wrong. Try again %d-time entering password", user1.getPswCounter())).exec();
                    }
                }
            }


            if (user1.getAdminRole().equals(Role.USER)) {
                Optional<User> byAdminRole = userRepository.findByAdminRole(Role.ADMIN);
                user1.setUserStatus(UserStatus.SERVICE_SELECTING);
                UserMessages userMessages = new UserMessages();
                userMessages.setUserTelegramId(message.from.id);
                userMessages.setMessage(message.text);
                byAdminRole.ifPresent(user -> userMessages.setAdminTelegramId(user.getUserTelegramId()));
                userMessages.setChatId(message.chat.id);
                userMessageRepository.save(userMessages);
                context.sendMessage(byAdminRole.get().getChatId(), String.format("New message from \"%s\": \nContent: %s",
                        message.from.first_name,
                        message.text)).exec();

                context.sendMessage(message.chat.id, "Message is sent.").exec();
                BotMenu.blogUserMenu(context, message.chat.id);


            }
        }
    }

//    public void onViewPosts(BotContext context, CallbackQuery callbackQuery) {
//        List<Post> posts = postRepository.findAllByStatus(PostStatus.PUBLISHED);
//        StringBuilder text = new StringBuilder();
//        for (Post post : posts) {
//            text.append("<b>Title:</b> ").append("<i>").append(post.getTitle()).append("</i>");
//            text.append("\n<b>Content:</b> ").append("<i>").append(post.getContent()).append("</i>");
//            text.append("\n<b>Author:</b> ").append(post.getFirstName()).append(" ").append(post.getLastName());
//            if (post.getUsername() != null) {
//                text.append(" (@").append(post.getUsername()).append(")");
//            }
//            text.append("\n\n");
//        }
//
//        context.sendMessage(callbackQuery.message.chat.id, text.toString())
//                .parseMode(ParseMode.HTML)
//                .exec();
//        BotMenu.startMenu(context, callbackQuery.message.chat.id);
//    }
}
