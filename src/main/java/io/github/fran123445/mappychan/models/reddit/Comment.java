package io.github.fran123445.mappychan.models.reddit;

public class Comment {
    private String body;
    private int upvotes;

    public Comment(String body, int upvotes) {
        this.body = body;
        this.upvotes = upvotes;
    }
}
