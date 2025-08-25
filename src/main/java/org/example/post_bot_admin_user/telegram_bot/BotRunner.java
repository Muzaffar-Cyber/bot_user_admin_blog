package org.example.post_bot_admin_user.telegram_bot;

import io.github.natanimn.BotClient;
import io.github.natanimn.filters.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BotRunner implements CommandLineRunner {
    private final BotListener botListener;


    @Override
    public void run(String... args) throws Exception {
        BotClient bot = KruzBot.getBot();

        bot.onMessage(filter -> filter.commands("start"), (context, message) -> {
            botListener.onStart(context, message);
        });
        bot.onCallback(filter -> filter.callbackData("admin"), (context, callbackQuery) -> {
            botListener.onEnterPassword(context, callbackQuery);
        });
        bot.onCallback(filter -> filter.callbackData("user"), (context, callbackQuery) -> {
            botListener.onEnterUser(context, callbackQuery);
        });
        bot.onCallback(filter -> filter.callbackData("create_post"), (context, callbackQuery) -> {
            botListener.onCreatePost(context, callbackQuery);
        });
        bot.onCallback(filter -> filter.callbackData("msg_to_admin"), (context, callbackQuery) -> {
            botListener.sendMessageToAdmin(context, callbackQuery);
        });

//        bot.onCallback(filter -> filter.callbackData("view_posts"), (context, callbackQuery) -> {
//            botListener.onViewPosts(context, callbackQuery);
//        });

        // Handle any text messages here if needed
        bot.onMessage(Filter::text, botListener::onHandleTextData);

        bot.startPolling();
    }
}
