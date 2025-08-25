package org.example.post_bot_admin_user.telegram_bot;

import io.github.natanimn.BotContext;
import io.github.natanimn.types.keyboard.InlineKeyboardButton;
import io.github.natanimn.types.keyboard.InlineKeyboardMarkup;

public class BotMenu {

    public static void blogAdminMenu(BotContext context, long chatId) {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        inlineMarkup.addKeyboard(
                new InlineKeyboardButton("\uFE0F Create Post", "create_post"),
                new InlineKeyboardButton("\uD83D\uDCC4 View Posts", "view_posts"),
                new InlineKeyboardButton("\uD83D\uDED2 View Users", "view_users")
        );
        context.sendMessage(chatId, "Press one button").replyMarkup(inlineMarkup).exec();
    }

    public static void blogUserMenu(BotContext context, long chatId) {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        inlineMarkup.addKeyboard(
                new InlineKeyboardButton("\uFE0F Send message to admin", "msg_to_admin"),
                new InlineKeyboardButton("\uD83D\uDCC4 View Posts", "view_posts")
        );
        context.sendMessage(chatId, "Press one button").replyMarkup(inlineMarkup).exec();
    }

    public static void selectRoll(BotContext context, long chatId) {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        inlineMarkup.addKeyboard(
                new InlineKeyboardButton("\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB Admin", "admin"),
                new InlineKeyboardButton("\uD83D\uDE4D\uD83C\uDFFB\u200D♂\uFE0F User", "user")
        );
        context.sendMessage(chatId, "Select your role ⬇\uFE0F").replyMarkup(inlineMarkup).exec();
    }
}
