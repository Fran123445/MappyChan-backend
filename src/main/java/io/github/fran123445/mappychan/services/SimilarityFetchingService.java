package io.github.fran123445.mappychan.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fran123445.mappychan.DTOs.SubredditDTO;
import io.github.fran123445.mappychan.models.SubredditBoardMatch;
import io.github.fran123445.mappychan.models.fourchan.Board;
import io.github.fran123445.mappychan.models.reddit.Comment;
import io.github.fran123445.mappychan.models.reddit.Post;
import io.github.fran123445.mappychan.models.reddit.Subreddit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SimilarityFetchingService {

    @Autowired
    HttpClient httpClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Map<String, Board> boardMap;

    @Value("${matching_service.api.url}")
    String matchingServiceURL;

    public List<SubredditBoardMatch> getMatches(Subreddit subreddit) throws IOException, URISyntaxException, InterruptedException {
        SubredditDTO subredditDTO = createSubredditDTO(subreddit);
        JsonNode responseJson = sendSubredditData(subredditDTO);

        return extractMatchesFromJson(subreddit, responseJson);
    }

    private SubredditDTO createSubredditDTO(Subreddit subreddit) {
        List<List<String>> postBodies = new ArrayList<>();
        List<List<Integer>> postUpvotes = new ArrayList<>();

        for (Post post : subreddit.getPosts()) {
            List<String> commentBodies = new ArrayList<>();
            List<Integer> commentUpvotes = new ArrayList<>();

            commentBodies.add(post.getTitle() + "\n\n" + post.getBody());
            commentUpvotes.add(post.getUpvotes());

            for (Comment comment : post.getComments()) {
                commentBodies.add(comment.getBody());
                commentUpvotes.add(comment.getUpvotes());
            }

            postBodies.add(commentBodies);
            postUpvotes.add(commentUpvotes);
        }

        return new SubredditDTO(postBodies, postUpvotes);
    }

    private JsonNode sendSubredditData(SubredditDTO subredditDTO) throws IOException, URISyntaxException, InterruptedException {
        String body = objectMapper.writeValueAsString(subredditDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(matchingServiceURL))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readTree(response.body());
    }

    private List<SubredditBoardMatch> extractMatchesFromJson(Subreddit subreddit, JsonNode matchesNode) {
        List<SubredditBoardMatch> matchList = new ArrayList<>();

        for (JsonNode matchNode : matchesNode) {
            String currentBoardName = matchNode.get(0).asText();
            double score = matchNode.get(1).asDouble();

            Board board = boardMap.get(currentBoardName);

            SubredditBoardMatch match = new SubredditBoardMatch(subreddit, board, score);

            matchList.add(match);
        }

        return matchList;
    }
}
