package io.github.fran123445.mappychan.models.reddit;

import java.util.List;

public class Subreddit {

    private String name;
    private List<Post> posts;

    public Subreddit(String name) {
        this.name = name;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
