package telegrambot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot {
    public void runTelegramJava(String botUserName, String botToken, String httpLink) {
        // Initialize Api Context
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
            botsApi.registerBot(new MyAmazingBot(botUserName, botToken, httpLink));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
