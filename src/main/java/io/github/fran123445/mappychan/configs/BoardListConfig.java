package io.github.fran123445.mappychan.configs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fran123445.mappychan.models.fourchan.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class BoardListConfig {

    @Autowired
    HttpClient httpClient;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${4chan.api.boards_url}")
    String boardsEndpointURL;

    @Bean
    public List<Board> boardList() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(boardsEndpointURL))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode boardsNode = objectMapper.readTree(response.body()).get("boards");

        List<Board> boardList = new ArrayList<>();

        for (JsonNode boardNode : boardsNode) {
            String boardName = boardNode.get("board").asText();
            String boardTitle = boardNode.get("title").asText();

            Board board = new Board(boardName, boardTitle);
            boardList.add(board);
        }

        return boardList;
    }

}
