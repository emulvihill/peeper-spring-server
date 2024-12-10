package com.snazzyrobot.peeper.service;

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

    public VideoSnap findById(long id) {
        return videoSnapRepository.findById(id).orElse(null);
    }

    public void deleteById(long id) {
        videoSnapRepository.deleteById(id);
    }

    public VideoSnap createVideoSnap(VideoSnapInput input) throws IOException {
        final OffsetDateTime date = OffsetDateTime.now();
        final VideoSnap latest = VideoSnap.builder().date(date).data(input.getData())
                .feed(feedRepository.getReferenceById(input.getFeedId())).build();
        String after = PatternUtil.stripBase64DataUriPrefix(input.getData());

        VideoSnap beforeSnap = videoSnapRepository.findTopByOrderByDateDesc().orElse(null);
        if (beforeSnap != null) {
            logger.info("Before snap: {}, {} ", beforeSnap.getId(), beforeSnap.getDate());
        }
        String before = beforeSnap != null ? PatternUtil.stripBase64DataUriPrefix(beforeSnap.getData()) : null;
        var persisted = videoSnapRepository.saveAndFlush(latest);
        // TODO: llama3.2-vision only supports single image.
        var comparison = before != null ? ollamaVisionService.compareImagesUsingCombining(before, after) :
                ollamaVisionService.describeImage(after);
        logger.info("Comparison: " + comparison);
        return persisted;
    }

    public List<VideoSnap> findAllForFeed(long feedId) {
        return videoSnapRepository.findByFeed(feedRepository.findById(feedId));
    }
}
