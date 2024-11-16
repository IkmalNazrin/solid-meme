/**
 * Handles messages and updates received by the Telegram bot.
 *
 * @author Ikmal Nazrin bin Aziz
 */
package my.uum;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

public class MessageHandler {

    /**
     * The bot instance to handle updates.
     */
    private final s294501_bot bot;

    /**
     * The client to interact with the News API.
     */
    private final NewsApiClient newsApiClient;

    private String selectedCountry = "";
    private String selectedCategory = "";
    private String searchQuery = "";
    private boolean isSearching = false;

    private final String[] COUNTRIES = {"Australia", "Canada", "Indonesia", "Ireland", "Malaysia", "New Zealand", "Nigeria", "Singapore", "South Africa", "United Kingdom", "United States"};
    private final String[] CATEGORIES = {"Business", "Entertainment", "Health", "Science", "Sports", "Technology"};

    /**
     * Constructs a MessageHandler instance.
     * @param bot The bot instance to handle updates
     * @param newsApiClient The client to interact with the News API
     */
    public MessageHandler(s294501_bot bot, NewsApiClient newsApiClient) {
        this.bot = bot;
        this.newsApiClient = newsApiClient;
    }

    /**
     * Handles incoming updates.
     * @param update The update received from the Telegram API
     * @param bot The bot instance
     */
    public void handleUpdate(Update update, s294501_bot bot) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText().trim();

            // Check if the user has entered the /start command
            if ("/start".equals(text)) {
                // Reset selected country, category, and search-related variables
                selectedCountry = "";
                selectedCategory = "";
                searchQuery = "";
                isSearching = false;

                // Send the help message instead of the start message
                bot.sendHelpMessage(message.getChatId().toString());
                return;
            }

            // Handle the /browse_news command
            if ("/browse_news".equals(text)) {
                // Reset selected country, category, and search-related variables
                selectedCountry = "";
                selectedCategory = "";
                searchQuery = "";
                isSearching = false;

                // Send the welcome message with country buttons
                bot.sendWelcomeMessageWithCountryButtons(message.getChatId().toString());
                return;
            }

            // Handle country selection or other commands here
            if (Arrays.asList(COUNTRIES).contains(text)) {
                if (isSearching) {
                    // Prompt the user to use /browse_news command instead
                    bot.sendMsg(message.getChatId().toString(), "\uD83D\uDEAB Please use /browse_news command to select a country and category instead of selecting a country during search.");
                    return;
                }

                selectedCountry = text;
                System.out.println("Selected country: " + selectedCountry);
                bot.sendCategorySelection(message.getChatId().toString());
                return;
            }

            // Check if the user has entered the /search_news command
            if ("/search_news".equals(text)) {
                // Reset selected country and category, and set isSearching flag to true
                selectedCountry = "";
                selectedCategory = "";
                searchQuery = "";
                isSearching = true;

                // Prompt the user to enter a keyword or phrase
                bot.sendMsg(message.getChatId().toString(), "▰▰▰▰▰▰▰ \n\n 🔎 Feeling curious?"
                        + "\n\n Use /search_news to search for news articles using 『 𝗸𝗲𝘆𝘄𝗼𝗿𝗱𝘀 𝗼𝗿 𝗽𝗵𝗿𝗮𝘀𝗲𝘀 』."
                        + "\n The world of information is at your fingertips!"
                        + "\n\n▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
                return;
            }

            // Check if the user is in search mode before allowing category selection or using the back button
            if (isSearching) {
                // Check if the user entered a keyword or phrase
                if (!Arrays.asList(CATEGORIES).contains(text) && !"Back".equalsIgnoreCase(text)) {
                    searchQuery = text;
                    String apiUrl = newsApiClient.constructNewsApiUrlWithQuery(searchQuery.toLowerCase());
                    System.out.println("Search API URL: " + apiUrl);
                    String news = newsApiClient.fetchNews(apiUrl);
                    bot.sendMsg(message.getChatId().toString(), news);
                    return;
                } else {
                    // Prompt the user to use /browse_news command instead
                    bot.sendMsg(message.getChatId().toString(), "\uD83D\uDEAB Please use /browse_news command to select a country and category instead of selecting a category during search.");
                    return;
                }
            }

            // Check if the user has selected the "Back" button
            if ("Back".equalsIgnoreCase(text)) {
                // Reset selected country and send country selection menu
                selectedCountry = "";
                bot.sendWelcomeMessageWithCountryButtons(message.getChatId().toString());
                return;
            }

            // Check if the user has selected a category
            if (!selectedCountry.isEmpty() && Arrays.asList(CATEGORIES).contains(text)) {
                selectedCategory = text;
                String apiUrl = newsApiClient.constructNewsApiUrl(selectedCountry.toLowerCase(), selectedCategory.toLowerCase());
                System.out.println("API URL: " + apiUrl);
                String news = newsApiClient.fetchNews(apiUrl);
                bot.sendMsg(message.getChatId().toString(), news);
                return;
            }

            // Handle invalid input
            bot.sendMsg(message.getChatId().toString(), "⚠\uFE0F❗Invalid input. Please try again.");
        }
    }
}
