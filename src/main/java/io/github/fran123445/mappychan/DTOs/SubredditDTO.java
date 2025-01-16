package io.github.fran123445.mappychan.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SubredditDTO {

    @JsonProperty("text_lists")
    List<List<String>> postBodies;

    @JsonProperty("weight_lists")
    List<List<Integer>> postUpvotes;

    public SubredditDTO(List<List<String>> postBodies, List<List<Integer>> postUpvotes) {
        this.postBodies = postBodies;
        this.postUpvotes = postUpvotes;
    }

    public List<List<String>> getPostBodies() {
        return postBodies;
    }

    public List<List<Integer>> getPostUpvotes() {
        return postUpvotes;
    }
}
