/**
 * Telegram bot class that extends TelegramLongPollingBot.
 *
 * @author Ikmal Nazrin bin Aziz
 */
package my.uum;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class s294501_bot extends TelegramLongPollingBot {
    private static final String[] COUNTRIES = {"Australia", "Canada", "Indonesia", "Ireland", "Malaysia", "New Zealand", "Nigeria", "Singapore", "South Africa", "United Kingdom", "United States"};
    private static final String[] CATEGORIES = {"Business", "Entertainment", "Health", "Science", "Sports", "Technology"};
    private String selectedCountry = "";
    private String selectedCategory = "";
    private static final int MAX_MESSAGE_LENGTH = 4096;
    private final MessageHandler messageHandler;
    private final NewsApiClient newsApiClient;

    /**
     * Constructor for the bot.
     * @param messageHandler The MessageHandler instance to handle incoming updates
     * @param newsApiClient The NewsApiClient instance to interact with the News API
     */
    public s294501_bot(MessageHandler messageHandler, NewsApiClient newsApiClient) {
        this.messageHandler = messageHandler;
        this.newsApiClient = newsApiClient;
    }

    /**
     * Returns the username of the bot.
     * @return Bot username
     */
    @Override
    public String getBotUsername() {
        return "s294501_bot";
    }

    /**
     * Returns the token of the bot.
     * @return Bot token
     */
    @Override
    public String getBotToken() {
        return "6379732126:AAH1L2zYBeDUjkOsqwwi2uM5FVmCHQB4X88";
    }

    /**
     * Handles updates received by the bot.
     * @param update The update received from the Telegram API
     */
    @Override
    public void onUpdateReceived(Update update) {
        messageHandler.handleUpdate(update, this);
    }

    /**
     * Sends a welcome message with country selection buttons.
     * @param chatId The ID of the chat to send the message to
     */
    // Inside sendWelcomeMessageWithCountryButtons method
    public void sendWelcomeMessageWithCountryButtons(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("▰▰▰▰▰▰▰\n\n"
                + "\uD83C\uDF0D Ready to explore the world of news?"
                + "\n\n Use /browse_news to 『 \uD835\uDE00\uD835\uDDF2\uD835\uDDF9\uD835\uDDF2\uD835\uDDF0\uD835\uDE01 \uD835\uDDEE \uD835\uDDF0\uD835\uDDFC\uD835\uDE02\uD835\uDDFB\uD835\uDE01\uD835\uDDFF\uD835\uDE06 \uD835\uDDEE\uD835\uDDFB\uD835\uDDF1 \uD835\uDDF0\uD835\uDDEE\uD835\uDE01\uD835\uDDF2\uD835\uDDF4\uD835\uDDFC\uD835\uDDFF\uD835\uDE06 』,"
                + " and let's go on an adventure together!"
                + "\n\n▰▰▰▰▰▰▰▰▰▰▰▰▰▰");

        // Create ReplyKeyboardMarkup object for country buttons
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);  // Make buttons fit on a single line

        // Create a list to hold rows of buttons (KeyboardRow)
        List<KeyboardRow> rows = new ArrayList<>();

        // Calculate the number of buttons per row
        int buttonsPerRow = 3;

        // Create multiple KeyboardRow objects for countries
        for (int i = 0; i < COUNTRIES.length; i += buttonsPerRow) {
            KeyboardRow row = new KeyboardRow();
            for (int j = i; j < Math.min(i + buttonsPerRow, COUNTRIES.length); j++) {
                row.add(new KeyboardButton(COUNTRIES[j]));
            }
            rows.add(row);
        }

        // Set the keyboard rows
        replyKeyboardMarkup.setKeyboard(rows);

        // Attach the keyboard to the message and send it
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMsg(chatId, "An error occurred while sending the welcome message. Please try again later.");
        }
    }

    /**
     * Sends category selection buttons after a country is selected.
     * @param chatId The ID of the chat to send the message to
     */
    // Inside sendCategorySelection method
    public void sendCategorySelection(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Please choose a category for " + selectedCountry + " news:");

        // Create ReplyKeyboardMarkup object for category buttons
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);  // Make buttons fit on a single line

        // Create a list to hold rows of buttons (KeyboardRow)
        List<KeyboardRow> rows = new ArrayList<>();

        // Create a KeyboardRow for the "Back" button
        KeyboardRow backRow = new KeyboardRow();
        backRow.add(new KeyboardButton("Back"));
        rows.add(backRow);

        // Create two KeyboardRow objects for categories, if there are an odd number of categories,
        // we add one empty row at the end
        for (int i = 0; i < CATEGORIES.length; i += 2) {
            KeyboardRow row = new KeyboardRow();
            row.addAll(Arrays.asList(new KeyboardButton(CATEGORIES[i]), new KeyboardButton(CATEGORIES[i + 1])));
            rows.add(row);
        }
        if (CATEGORIES.length % 2 != 0) {
            rows.add(new KeyboardRow()); // Add an empty row if there's an odd number of categories
        }

        // Set the keyboard rows
        replyKeyboardMarkup.setKeyboard(rows);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a help message with available commands.
     * @param chatId The ID of the chat to send the message to
     */
    // Inside sendHelpMessage method
    public void sendHelpMessage(String chatId) {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("∘₊✧──────────────────────────✧₊∘"
                + "\n\n📰 Welcome to the News API Bot!\n\n");
        helpMessage.append("『 Here are some available commands:\n");
        helpMessage.append(" \uD83D\uDCDD  /start - Get bot usage help\n");
        helpMessage.append(" \uD83C\uDF0E  /browse_news - Browse news by country and category\n");
        helpMessage.append(" \uD83D\uDD0D  /search_news - Search for news articles using keywords or phrases\n\n");
        helpMessage.append("You can also select the '◀ \uFE0F \uD835\uDDD5\uD835\uDDEE\uD835\uDDF0\uD835\uDDF8' button to return to the previous menu.\n\n");
        helpMessage.append("╰┈➤  Enjoy exploring the latest news!\n");

        sendMsg(chatId, helpMessage.toString());
    }

    /**
     * Sends a message to the specified chat.
     * @param chatId The ID of the chat to send the message to
     * @param text The text of the message
     */
    // Inside sendMsg method
    public void sendMsg(String chatId, String text) {
        if (isJSONValid(text)) { // Check if the text is a valid JSON
            handleNewsApiResponse(chatId, text); // Handle the JSON response
        } else {
            // Send text message as usual
            if (text.length() > MAX_MESSAGE_LENGTH) {
                // Truncate the message if it's too long
                text = text.substring(0, MAX_MESSAGE_LENGTH - 3) + "...";
            }
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if a given string is a valid JSON.
     * @param jsonInString The JSON string to check
     * @return True if the string is a valid JSON, false otherwise
     */
    // Inside isJSONValid method
    private boolean isJSONValid(String jsonInString) {
        try {
            new JSONObject(jsonInString);
            return true;
        } catch(JSONException e) {
            return false;
        }
    }

    /**
     * Handles the JSON response from the News API.
     * @param chatId The ID of the chat to send the message to
     * @param jsonResponse The JSON response from the News API
     */
    // Inside handleNewsApiResponse method
    private void handleNewsApiResponse(String chatId, String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Check if the status is "ok"
            String status = jsonObject.optString("status");
            if (!"ok".equalsIgnoreCase(status)) {
                sendMsg(chatId, "\uD83D\uDC94 No news articles found.");
                return;
            }

            // Get the array of articles
            JSONArray articlesArray = jsonObject.optJSONArray("articles");
            if (articlesArray == null || articlesArray.length() == 0) {
                sendMsg(chatId, "\uD83D\uDC94 No news articles found.");
                return;
            }

            // Iterate through articles to find URLs
            int urlsSent = 0;
            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject articleObject = articlesArray.getJSONObject(i);
                JSONObject sourceObject = articleObject.optJSONObject("source");

                // Check if source and article are not null
                if (sourceObject != null && articleObject != null) {
                    String sourceName = sourceObject.optString("name", "Source not available");
                    String author = articleObject.optString("author", "Author not available");
                    String title = articleObject.optString("title", "Title not available");
                    String description = articleObject.optString("description", "Description not available");
                    String url = articleObject.optString("url", ""); // Get the URL of the full article
                    String publishedAt = articleObject.optString("publishedAt", "Date not available");

                    // Check if title and URL are not empty
                    if (!title.isEmpty() && !url.isEmpty()) {
                        // Format publishedAt date and time to Malaysia Time (GMT+8)
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // API returns time in UTC
                        Date date = inputFormat.parse(publishedAt);

                        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a");
                        outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur")); // Convert to Malaysia Time (GMT+8)
                        String formattedDate = outputFormat.format(date);

                        // Construct message for this article
                        StringBuilder message = new StringBuilder();
                        message.append("✦•······················•✦•······················•✦\n\n" + "\uD835\uDDE6\uD835\uDDFC\uD835\uDE02\uD835\uDDFF\uD835\uDDF0\uD835\uDDF2: ").append(sourceName).append("\n\n"); // Source:
                        message.append("\uD835\uDDE7\uD835\uDDF6\uD835\uDE01\uD835\uDDF9\uD835\uDDF2: ").append(title).append("\n\n"); // Title:
                        message.append("\uD835\uDDD4\uD835\uDE02\uD835\uDE01\uD835\uDDF5\uD835\uDDFC\uD835\uDDFF: ").append(author).append("\n\n"); // Author:
                        message.append("\uD835\uDDD7\uD835\uDDF2\uD835\uDE00\uD835\uDDF0\uD835\uDDFF\uD835\uDDF6\uD835\uDDFD\uD835\uDE01\uD835\uDDF6\uD835\uDDFC\uD835\uDDFB: ").append(description).append("\n\n"); // Description:
                        message.append("\uD835\uDDE3\uD835\uDE02\uD835\uDDEF\uD835\uDDF9\uD835\uDDF6\uD835\uDE00\uD835\uDDF5\uD835\uDDF2\uD835\uDDF1 \uD835\uDDD4\uD835\uDE01: ").append(formattedDate).append("\n\n"); // Published At:
                        message.append("\uD835\uDDE8\uD835\uDDE5\uD835\uDDDF: ").append(url).append("\n\n"); // URL:

                        // Send message for this article
                        sendMsg(chatId, message.toString());

                        urlsSent++;

                        // Check if maximum number of URLs sent
                        if (urlsSent >= 5) {
                            break;
                        }
                    }
                }
            }

            // If no URLs were sent, send a message indicating so
            if (urlsSent == 0) {
                sendMsg(chatId, "❌ No valid news articles found with URLs.");
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
            sendMsg(chatId, "An error occurred while processing news. Please try again later.");
        }
    }

}
