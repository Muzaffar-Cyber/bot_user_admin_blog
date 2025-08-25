package org.example.post_bot_admin_user.telegram_bot;

import io.github.natanimn.BotClient;

public class KruzBot {
    private static BotClient botClient;

    private KruzBot() {
    }

    public static BotClient getBot() {
        if (botClient == null) {
            String TOKEN = "8156700580:AAHc99X85wS2BxB_zrQ9jO5g7qIDUF2CzkA";
            botClient = new BotClient(TOKEN);
            return botClient;
        }
        return botClient;
    }
}
