/**
 * Handles HTTP requests to the News API.
 *
 * @author Ikmal Nazrin bin Aziz
 */
package my.uum;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class NewsApiClient {
    /**
     * The API key for accessing the News API.
     */
    private static final String API_KEY = "f78a1f4c3e1e403f9a5a8e55867eb7b1";

    /**
     * Fetches news articles from the News API.
     * @param apiUrl The URL of the News API endpoint
     * @return The response from the News API
     */
    public String fetchNews(String apiUrl) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiUrl);

        try {
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                // If the request is successful (status code 200), read and return the response
                InputStream responseStream = response.getEntity().getContent();
                Scanner scanner = new Scanner(responseStream).useDelimiter("\\A");
                String responseText = scanner.hasNext() ? scanner.next() : "";
                System.out.println("API Response: " + responseText); // Print the API response for debugging
                return responseText;
            } else {
                // If the request fails, log the error and return an error message
                System.err.println("Failed to fetch news. HTTP status code: " + statusCode);
                return "Failed to fetch news. Please try again later.";
            }
        } catch (IOException e) {
            // If an exception occurs during the request, log the error and return an error message
            e.printStackTrace();
            return "⚠\uFE0F Failed to fetch news. Please try again later.";
        }
    }

    /**
     * Constructs the URL for retrieving top headlines by country and category.
     * @param country The selected country
     * @param category The selected category
     * @return The constructed API URL
     */
    public String constructNewsApiUrl(String country, String category) {
        // Map the selected country to its ISO 2-letter country code for the News API
        String iso2CountryCode = "";
        switch (country.toLowerCase()) {
            case "australia":
                iso2CountryCode = "au";
                break;
            case "canada":
                iso2CountryCode = "ca";
                break;
            case "indonesia":
                iso2CountryCode = "id";
                break;
            case "ireland":
                iso2CountryCode = "ie";
                break;
            case "malaysia":
                iso2CountryCode = "my";
                break;
            case "new zealand":
                iso2CountryCode = "nz";
                break;
            case "nigeria":
                iso2CountryCode = "ng";
                break;
            case "singapore":
                iso2CountryCode = "sg";
                break;
            case "south africa":
                iso2CountryCode = "za";
                break;
            case "united kingdom":
                iso2CountryCode = "gb";
                break;
            case "united states":
                iso2CountryCode = "us";
                break;
        }
        // Construct the URL using the ISO 2-letter country code and category, along with the API key
        return "https://newsapi.org/v2/top-headlines?country=" + iso2CountryCode + "&category=" + category + "&apiKey=" + API_KEY;
    }

    /**
     * Constructs the URL for searching news articles by query.
     * @param query The search query
     * @return The constructed API URL
     */
    public String constructNewsApiUrlWithQuery(String query) {
        // Encode spaces in the query to make it URL-friendly
        query = query.replaceAll(" ", "%20");

        // Construct the URL for searching news articles using the provided query and the API key
        return "https://newsapi.org/v2/everything?q=" + query + "&apiKey=" + API_KEY;
    }
}
