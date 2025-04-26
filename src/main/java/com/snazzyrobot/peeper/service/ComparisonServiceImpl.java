package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.dto.ComparisonFormat;
import com.snazzyrobot.peeper.entity.CompareProfile;
import com.snazzyrobot.peeper.entity.SnapComparison;
import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.repository.CompareProfileRepository;
import com.snazzyrobot.peeper.repository.FeedRepository;
import com.snazzyrobot.peeper.repository.SnapComparisonRepository;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import com.snazzyrobot.peeper.utility.PatternUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class ComparisonServiceImpl implements ComparisonService {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonServiceImpl.class);

    private static final String VIDEO_SNAP_NOT_FOUND = "VideoSnap with id %d not found";

    private final FeedRepository feedRepository;
    private final SnapComparisonRepository snapComparisonRepository;
    private final VideoSnapRepository videoSnapRepository;
    private final VisionService visionService;
    private final CompareProfileRepository compareProfileRepository;

    public ComparisonServiceImpl(VideoSnapRepository videoSnapRepository,
                                 FeedRepository feedRepository,
                                 SnapComparisonRepository snapComparisonRepository,
                                 VisionService visionService,
                                 CompareProfileRepository compareProfileRepository) {
        this.feedRepository = feedRepository;
        this.snapComparisonRepository = snapComparisonRepository;
        this.videoSnapRepository = videoSnapRepository;
        this.visionService = visionService;
        this.compareProfileRepository = compareProfileRepository;
    }

    public SnapComparison compareVideoSnapsById(Long id1, Long id2, String profile) throws IOException {

        logger.info("compareVideoSnapsById, id {} to id {}, profile {}", id1, id2, profile);

        // Find or create the CompareProfile
        CompareProfile compareProfile = compareProfileRepository.findByName(profile)
                .orElseThrow(() -> new IllegalArgumentException("Compare profile '" + profile + "' not found."));

        var snapSequence = getOrderedSnapSequence(id1, id2);
        Map.Entry<String, ComparisonFormat> resultEntry = visionService.compareImages(
                PatternUtil.stripBase64DataUriPrefix(snapSequence.before.getData()),
                PatternUtil.stripBase64DataUriPrefix(snapSequence.after.getData()));

        var rawResult = resultEntry.getKey();
        var result = resultEntry.getValue();

        var snapComparison = SnapComparison.builder()
                .current(snapSequence.after)
                .previous(snapSequence.before)
                .feed(snapSequence.after.getFeed())
                .rawComparison(rawResult)
                .resultDetected(!result.getComparisons().isEmpty())
                .comparisons(result.getComparisons())
                .numPersons(result.getNumPersons())
                .build();

        return snapComparisonRepository.save(snapComparison);
    }

    @Override
    public List<SnapComparison> findAllForFeed(long feedId) {
        return snapComparisonRepository.findAllByFeed(feedRepository.findById(feedId));
    }

    private SnapSequence getOrderedSnapSequence(Long id1, Long id2) {
        VideoSnap snap1 = videoSnapRepository.findById(id1).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id1)));
        VideoSnap snap2 = videoSnapRepository.findById(id2).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id2)));

        if (!snap1.getFeed().equals(snap2.getFeed())) {
            throw new IllegalArgumentException("Cannot compare two snaps from different feeds");
        }

        var isSnap1Earlier = snap1.getCreated().isBefore(snap2.getCreated());

        return isSnap1Earlier ? new SnapSequence(snap1, snap2) : new SnapSequence(snap2, snap1);
    }

    private record SnapSequence(VideoSnap before, VideoSnap after) {
    }

    private static Stream<String> findValidComparisons(String str) {
        return Arrays.stream(str.split("\\s*\\*\\*\\*\\s*")).skip(1);
    }
}
