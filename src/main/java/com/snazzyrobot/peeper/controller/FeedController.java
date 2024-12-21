package com.snazzyrobot.peeper.controller;

import com.snazzyrobot.peeper.entity.Feed;
import com.snazzyrobot.peeper.entity.FeedInput;
import com.snazzyrobot.peeper.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FeedController {

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @QueryMapping
    public List<Feed> feeds() {
        return feedService.list();
    }

    @QueryMapping
    public Feed feed(@Argument Long id) {
        return feedService.findById(id);
    }

    @MutationMapping
    public Feed createFeed(@Argument FeedInput input) {
        return feedService.createFeed(input);
    }

    @MutationMapping
    public Boolean deleteFeed(@Argument Long id) {
        feedService.deleteById(id);
        return true;
    }
}