package telegrambot;

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

            String newMessage = message_text.equals("951-17") ?  "Timetable of 951-17 is blablablabla go to home and learn scala" :  "hiii";
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
