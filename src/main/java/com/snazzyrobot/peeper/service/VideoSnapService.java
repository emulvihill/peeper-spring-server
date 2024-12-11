package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.Feed;
import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.entity.VideoSnapInput;
import com.snazzyrobot.peeper.repository.FeedRepository;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import com.snazzyrobot.peeper.utility.PatternUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class VideoSnapService {
    private static final Logger logger = LoggerFactory.getLogger(VideoSnapService.class);

    private final VideoSnapRepository videoSnapRepository;
    private final FeedRepository feedRepository;
    private final OllamaVisionService ollamaVisionService;

    public VideoSnapService(VideoSnapRepository videoSnapRepository, FeedRepository feedRepository,
                            OllamaVisionService ollamaVisionService) {
        this.videoSnapRepository = videoSnapRepository;
        this.feedRepository = feedRepository;
        this.ollamaVisionService = ollamaVisionService;
    }

    public List<VideoSnap> list() {
        return videoSnapRepository.findAll();
    }

    public VideoSnap findById(Long id) {
        return videoSnapRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        videoSnapRepository.deleteById(id);
    }

    public VideoSnap createVideoSnap(VideoSnapInput input) {
        logger.info("Creating video snap for feed: {}", input.getFeedId());

        OffsetDateTime date = OffsetDateTime.now();
        Feed feed = feedRepository.getReferenceById(input.getFeedId());
        VideoSnap snap = VideoSnap.builder().date(date).data(input.getData())
                .feed(feed).build();
        VideoSnap persistedSnap = videoSnapRepository.save(snap);
        return persistedSnap;
    }

    public VideoSnap experimentalCreateAndCompareVideoSnap(VideoSnapInput input) throws IOException {
        logger.info("Creating video snap for feed: {}", input.getFeedId());

        final OffsetDateTime date = OffsetDateTime.now();
        final VideoSnap latest = VideoSnap.builder().date(date).data(input.getData())
                .feed(feedRepository.getReferenceById(input.getFeedId())).build();
        String afterData = PatternUtil.stripBase64DataUriPrefix(input.getData());

        VideoSnap prevSnap = videoSnapRepository.findTopByOrderByDateDesc().orElse(null);
        String prevData = prevSnap != null ? PatternUtil.stripBase64DataUriPrefix(prevSnap.getData()) : null;
        var persistedSnap = videoSnapRepository.save(latest);

        if (prevSnap != null) {
            logger.info("Previous snap: {}, {} ", prevSnap.getId(), prevSnap.getDate());
        }
        logger.info("Persisted snap: {}, {} ", persistedSnap.getId(), persistedSnap.getDate());

        // TODO: llama3.2-vision only supports single image.
        var comparison = prevData != null ? ollamaVisionService.compareImagesUsingCombining(prevData, afterData) :
                ollamaVisionService.describeImage(afterData);
        logger.info("Comparison: " + comparison);

        return persistedSnap;
    }

    public List<VideoSnap> findAllForFeed(Long feedId) {
        return videoSnapRepository.findByFeed(feedRepository.findById(feedId));
    }
}
