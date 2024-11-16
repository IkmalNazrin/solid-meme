/**
 * MainClass initializes and runs the Telegram bot.
 *
 * @author Ikmal Nazrin bin Aziz
 */
package my.uum;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class MainClass {
    /**
     * Main method to start the Telegram bot.
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Create an instance of NewsApiClient to fetch news data
            NewsApiClient newsApiClient = new NewsApiClient();

            // Create an instance of MessageHandler and pass a bot instance and the newsApiClient
            MessageHandler messageHandler = new MessageHandler(new s294501_bot(null, null), newsApiClient);

            // Create an instance of s294501_bot and pass MessageHandler and NewsApiClient instances
            s294501_bot bot = new s294501_bot(messageHandler, newsApiClient);

            // Register your bot with the Telegram Bot API
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) { // Catch any exceptions that occur during bot registration
            e.printStackTrace(); // Print stack trace of the exception
        }
    }
}
