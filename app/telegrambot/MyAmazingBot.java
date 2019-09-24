package telegrambot;

import controllers.TimetableController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
public class MyAmazingBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {


        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            String newMessage = TimetableController.hasGroup("monday", message_text);
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
        return TelegramCredentials.getTelegramCredentials("./conf/application_local.conf").username();
    }

    @Override
    public String getBotToken() {
        return TelegramCredentials.getTelegramCredentials("./conf/application_local.conf").token();
    }
}
