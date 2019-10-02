package telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyAmazingBot extends TelegramLongPollingBot {

    private final String botUserName;
    private final String botToken;
    private final String httpLink;

    public MyAmazingBot(final String botUserName, final String botToken, final String httpLink) {
        this.botUserName = botUserName;
        this.botToken = botToken;
        this.httpLink = httpLink;
    }

    @Override
    public void onUpdateReceived(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String newMessage = SendToServer.callApiAndSendMsg(message_text, new MyAmazingBot(botUserName, botToken, httpLink).httpLink);
            SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(newMessage);

            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return new MyAmazingBot(botUserName, botToken, httpLink).botUserName;
    }

    @Override
    public String getBotToken() {
        return new MyAmazingBot(botUserName, botToken, httpLink).botToken;
    }

}
