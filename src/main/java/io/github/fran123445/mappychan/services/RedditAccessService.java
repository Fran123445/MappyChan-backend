package io.github.fran123445.mappychan.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class RedditAccessService {

    @Value("${reddit.api.client_id}")
    String clientId;

    @Value("${reddit.api.client_secret}")
    String clientSecret;

    @Value("${reddit.api.token_url}")
    String tokenUrl;

    @Value("${reddit.api.base_url}")
    String baseUrl;

    @Value("${reddit.api.user_agent}")
    String userAgent;

    @Autowired
    HttpClient httpClient;

    @Autowired
    ObjectMapper objectMapper;

    String token;

    @PostConstruct
    public void initialize() throws URISyntaxException {
        try {
            this.token = acquireToken();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error acquiring Reddit API token", e);
        }
    }

    public String getSubredditJson(String subredditName) {
        String json;

        try {
            HttpRequest request = buildApiRequest(baseUrl + subredditName + "/hot/?limit=10" );

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            json = response.body();
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        return json;
    }

    private String acquireToken() throws URISyntaxException, InterruptedException, IOException {
        String authString = clientId + ":" + clientSecret;
        String encodedString = Base64.getEncoder().encodeToString(authString.getBytes());

        String grantType = "grant_type=client_credentials";

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Basic " + encodedString)
                .header("User-Agent", userAgent)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(grantType))
                .uri(new URI(tokenUrl))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP Error. Status: " + response.statusCode() + " - " + response.body());
        }

        String responseBody = response.body();
        JsonNode json = objectMapper.readTree(responseBody);

        return json.get("access_token").asText();
    }

    private HttpRequest buildApiRequest(String url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("User-Agent", userAgent)
                .uri(new URI(url))
                .build();
    }
}
