package io.github.fran123445.mappychan.models;

import io.github.fran123445.mappychan.models.fourchan.Board;
import io.github.fran123445.mappychan.models.reddit.Subreddit;

public class SubredditBoardMatch {

    private Subreddit subreddit;
    private Board board;
    private double score;

    public SubredditBoardMatch(Subreddit subreddit, Board board, double score) {
        this.subreddit = subreddit;
        this.board = board;
        this.score = score;
    }
}
