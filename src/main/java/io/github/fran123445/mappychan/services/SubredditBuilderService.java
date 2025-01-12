package io.github.fran123445.mappychan.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fran123445.mappychan.models.reddit.Comment;
import io.github.fran123445.mappychan.models.reddit.Post;
import io.github.fran123445.mappychan.models.reddit.Subreddit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubredditBuilderService {

    @Autowired
    RedditAccessService redditAccess;

    @Autowired
    ObjectMapper objectMapper;

    public Subreddit build(String subredditName) throws JsonProcessingException {
        Subreddit subreddit = new Subreddit(subredditName);

        String subredditJson = redditAccess.getSubredditJson(subredditName);

        JsonNode subredditNode = objectMapper.readTree(subredditJson);

        List<Post> postList = getPosts(subredditName, subredditNode);

        subreddit.setPosts(postList);

        return subreddit;
    }

    private List<Post> getPosts(String subredditName, JsonNode subredditNode) throws JsonProcessingException {
        List<Post> postList = new ArrayList<>();
        JsonNode postListNode = subredditNode.get("data").get("children");

        for (JsonNode postNode : postListNode) {
            JsonNode postData = postNode.get("data");

            Post post = new Post(postData.get("id").asText(),
                    postData.get("title").asText(),
                    postData.get("selftext").asText(),
                    postData.get("score").asInt());

            List<Comment> commentList = getComments(subredditName, post.getId());

            post.setComments(commentList);

            postList.add(post);
        }

        return postList;
    }

    private List<Comment> getComments(String subredditName, String postId) throws JsonProcessingException {
        List<Comment> commentList = new ArrayList<>();

        String postJson = redditAccess.getPostJson(subredditName, postId);
        JsonNode postNode = objectMapper.readTree(postJson);

        JsonNode commentListNode = postNode.get(1).get("data").get("children"); // idx 0 is the post, 1 is the comments

        getCommentFromNode(commentListNode, commentList);

        return commentList;
    }

    private void getCommentFromNode(JsonNode commentListNode, List<Comment> commentList) {
        for (JsonNode commentNode : commentListNode) {
            if (!commentNode.get("kind").asText().equals("t1")) {
                continue;
            }

            JsonNode commentData = commentNode.get("data");
            String body = commentData.get("body").asText();
            int upvotes = commentData.get("score").asInt();

            Comment comment = new Comment(body, upvotes);

            commentList.add(comment);

            if (commentData.has("replies") && !commentData.get("replies").isEmpty()) {
                JsonNode replies = commentData.get("replies").get("data").get("children");
                getCommentFromNode(replies, commentList);
            }
        }
    }
}
