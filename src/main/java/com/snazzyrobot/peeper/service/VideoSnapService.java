package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.entity.VideoSnapInput;
import com.snazzyrobot.peeper.repository.FeedRepository;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class VideoSnapService {

    private final VideoSnapRepository videoSnapRepository;
    private final FeedRepository feedRepository;

    public VideoSnapService(VideoSnapRepository videoSnapRepository, FeedRepository feedRepository) {
        this.videoSnapRepository = videoSnapRepository;
        this.feedRepository = feedRepository;
    }

    public List<VideoSnap> list() {
        return videoSnapRepository.findAll();
    }

    public VideoSnap findById(long id) {
        return videoSnapRepository.findById(id).orElse(null);
    }

    public void deleteById(long id) {
        videoSnapRepository.deleteById(id);
    }

    public VideoSnap createVideoSnap(VideoSnapInput input) {
        final OffsetDateTime date = OffsetDateTime.now();
        final VideoSnap r = VideoSnap.builder().date(date).data(input.getData())
                .feed(feedRepository.getReferenceById(input.getFeedId())).build();

        return videoSnapRepository.save(r);
    }

    public List<VideoSnap> findAllForFeed(long feedId) {
        return videoSnapRepository.findByFeed(feedRepository.findById(feedId));
    }
}
