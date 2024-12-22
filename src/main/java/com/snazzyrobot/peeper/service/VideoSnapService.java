package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.Feed;
import com.snazzyrobot.peeper.entity.SnapComparison;
import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.entity.VideoSnapInput;
import com.snazzyrobot.peeper.exception.ResourceNotFoundException;
import com.snazzyrobot.peeper.repository.FeedRepository;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;

@Validated
@Service
public class VideoSnapService {
    private static final Logger logger = LoggerFactory.getLogger(VideoSnapService.class);

    private final VideoSnapRepository videoSnapRepository;
    private final FeedRepository feedRepository;
    private final ComparisonService comparisonService;
    private final AsyncComparisonService asyncComparisonService;

    public VideoSnapService(VideoSnapRepository videoSnapRepository,
                            FeedRepository feedRepository,
                            ComparisonService comparisonService,
                            AsyncComparisonService asyncComparisonService) {
        this.videoSnapRepository = videoSnapRepository;
        this.feedRepository = feedRepository;
        this.comparisonService = comparisonService;
        this.asyncComparisonService = asyncComparisonService;
    }

    public List<VideoSnap> list() {
        return videoSnapRepository.findAll();
    }

    public VideoSnap findById(Long id) {
        return videoSnapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VideoSnap not found with id: " + id));
    }

    public void deleteById(Long id) {
        videoSnapRepository.deleteById(id);
    }

    public VideoSnap createVideoSnap(@NotNull VideoSnapInput input) {
        validateInput(input);

        Feed feed = feedRepository.findById(input.getFeedId())
                .orElseThrow(() -> new ResourceNotFoundException("Feed not found with id: " + input.getFeedId()));
        VideoSnap snap = VideoSnap.builder()
                .data(input.getData())
                .feed(feed)
                .build();

        var savedSnap = videoSnapRepository.save(snap);
        asyncComparisonService.compareWithPreviousSnap(savedSnap);
        return savedSnap;
    }

    public SnapComparison createAndCompareVideoSnap(@NotNull VideoSnapInput input) throws IOException {
        validateInput(input);

        final OffsetDateTime date = OffsetDateTime.now();
        final Feed feed = feedRepository.findById(input.getFeedId())
                .orElseThrow(() -> new ResourceNotFoundException("Feed not found with id: " + input.getFeedId()));
        final VideoSnap latest = VideoSnap.builder().data(input.getData())
                .feed(feed).build();

        VideoSnap prevSnap = videoSnapRepository.findTopByOrderByCreatedDesc().orElse(null);
        var persistedSnap = videoSnapRepository.save(latest);

        if (prevSnap != null) {
            return comparisonService.compareVideoSnapsById(prevSnap.getId(), persistedSnap.getId());
        } else {
            return SnapComparison.builder().current(persistedSnap).previous(null).comparison(List.of("Previous image is not available for comparison.")).build();
        }
    }

    public List<VideoSnap> findAllForFeed(Long feedId) {
        return videoSnapRepository.findByFeed(feedRepository.findById(feedId));
    }

    private void validateInput(VideoSnapInput input) {
        if (input.getData() == null || input.getData().isEmpty()) {
            throw new IllegalArgumentException("Video snap data cannot be empty");
        }
        if (input.getFeedId() == null) {
            throw new IllegalArgumentException("Feed ID cannot be null");
        }
    }
}
