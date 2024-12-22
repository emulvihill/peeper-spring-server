package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.SnapComparison;
import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.repository.SnapComparisonRepository;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import com.snazzyrobot.peeper.utility.PatternUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ComparisonServiceImpl implements ComparisonService {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonServiceImpl.class);

    private static final String VIDEO_SNAP_NOT_FOUND = "VideoSnap with id %d not found";

    private final ComparisonProcessorService comparisonProcessorService;
    private final SnapComparisonRepository snapComparisonRepository;
    private final VideoSnapRepository videoSnapRepository;
    private final VisionService visionService;

    public ComparisonServiceImpl(ComparisonProcessorService comparisonProcessorService,
                                 VideoSnapRepository videoSnapRepository,
                                 SnapComparisonRepository snapComparisonRepository,
                                 VisionService visionService) {
        this.comparisonProcessorService = comparisonProcessorService;
        this.snapComparisonRepository = snapComparisonRepository;
        this.videoSnapRepository = videoSnapRepository;
        this.visionService = visionService;
    }

    public SnapComparison compareVideoSnapsById(Long id1, Long id2) throws IOException {
        logger.info("compareVideoSnapsById, id {} to id {}", id1, id2);
        var snapSequence = getOrderedSnapSequence(id1, id2);
        ChatResponse chatResponse = visionService.compareImages(
                PatternUtil.stripBase64DataUriPrefix(snapSequence.before.getData()),
                PatternUtil.stripBase64DataUriPrefix(snapSequence.after.getData()));

        var comparisons = comparisonProcessorService.processComparisonResponse(snapSequence.before, snapSequence.after, chatResponse);

        var update = SnapComparison.builder().current(snapSequence.after).previous(snapSequence.before).comparison(comparisons).build();
        snapComparisonRepository.save(update);

        return update;
    }

    private SnapSequence getOrderedSnapSequence(Long id1, Long id2) {
        VideoSnap snap1 = videoSnapRepository.findById(id1).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id1)));
        VideoSnap snap2 = videoSnapRepository.findById(id2).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id2)));

        var isSnap1Earlier = snap1.getDate().isBefore(snap2.getDate());

        return isSnap1Earlier ? new SnapSequence(snap1, snap2) : new SnapSequence(snap2, snap1);
    }

    private record SnapSequence(VideoSnap before, VideoSnap after) {
    }
}