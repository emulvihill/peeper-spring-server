package com.snazzyrobot.peeper.controller;

import com.snazzyrobot.peeper.dto.VideoUpdate;
import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.entity.VideoSnapInput;
import com.snazzyrobot.peeper.service.VideoSnapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class VideoSnapController {

    private static final Logger logger = LoggerFactory.getLogger(VideoSnapController.class);

    private final VideoSnapService videoSnapService;

    public VideoSnapController(@Autowired VideoSnapService videoSnapService) {
        this.videoSnapService = videoSnapService;
    }

    @QueryMapping
    public VideoSnap videoSnap(@Argument String id) {
        return videoSnapService.findById(Long.parseLong(id));
    }

    @QueryMapping
    public List<VideoSnap> videoSnaps() {
        return videoSnapService.list();
    }

    @QueryMapping
    public List<VideoSnap> videoSnapsForFeed(@Argument String feedId) {
        return videoSnapService.findAllForFeed(Long.parseLong(feedId));
    }

    @MutationMapping
    public VideoSnap createVideoSnap(@Argument VideoSnapInput input) throws IOException {
        return videoSnapService.createVideoSnap(input);
    }

    @MutationMapping
    public VideoUpdate createAndCompareVideoSnap(@Argument VideoSnapInput input) throws IOException {
        logger.info("Creating new video snap");
        return videoSnapService.createAndCompareVideoSnap(input);
    }

    @MutationMapping
    public Boolean deleteVideoSnap(@Argument Long id) {
        videoSnapService.deleteById(id);
        return true;
    }
}