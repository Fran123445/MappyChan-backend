package io.github.fran123445.mappychan.models.reddit;

import java.util.List;

public class Post {

    private String id;
    private String title;
    private String body;
    private int upvotes;
    private List<Comment> comments;

    public Post(String id, String title, String body, int upvotes) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.upvotes = upvotes;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
