
package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.Feed;
import com.snazzyrobot.peeper.entity.FeedInput;
import com.snazzyrobot.peeper.repository.FeedRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    private final FeedRepository feedRepository;

    public FeedService(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    public List<Feed> list() {
        return feedRepository.findAll();
    }

    public Feed findById(Long id) {
        return feedRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        feedRepository.deleteById(id);
    }

    public Feed createFeed(FeedInput input) {
        final Feed r = Feed.builder()
                .userId(input.getUserId())
                .name(input.getName())
                .build();

        return feedRepository.save(r);
    }
}
