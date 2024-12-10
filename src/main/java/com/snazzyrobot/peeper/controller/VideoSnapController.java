package com.snazzyrobot.peeper.controller;

import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.entity.VideoSnapInput;
import com.snazzyrobot.peeper.service.VideoSnapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class VideoSnapController {

    private final VideoSnapService videosnapService;

    public VideoSnapController(@Autowired VideoSnapService videosnapService) {
        this.videosnapService = videosnapService;
    }

    @QueryMapping
    public VideoSnap videoSnap(@Argument String id) {
        return videosnapService.findById(Long.parseLong(id));
    }

    @QueryMapping
    public List<VideoSnap> videoSnaps() {
        return videosnapService.list();
    }

    @QueryMapping
    public List<VideoSnap> videoSnapsForFeed(@Argument String feedId) {
        return videosnapService.findAllForFeed(Long.parseLong(feedId));
    }

    @MutationMapping
    public VideoSnap createVideoSnap(@Argument VideoSnapInput input) throws IOException {
        return videosnapService.createVideoSnap(input);
    }

    @MutationMapping
    public Boolean deleteVideoSnap(@Argument Long id) {
        videosnapService.deleteById(id);
        return true;
    }
}