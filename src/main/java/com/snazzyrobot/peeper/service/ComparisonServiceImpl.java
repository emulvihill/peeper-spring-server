package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.ComparisonResult;
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
import java.util.List;

@Service
public class ComparisonServiceImpl implements ComparisonService {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonServiceImpl.class);

    private static final String VIDEO_SNAP_NOT_FOUND = "VideoSnap with id %d not found";

    private final ComparisonProcessorService comparisonProcessorService;
    private final SnapComparisonRepository snapComparisonRepository;
    private final VideoSnapRepository videoSnapRepository;
    private final OllamaVisionService ollamaVisionService;

    public ComparisonServiceImpl(ComparisonProcessorService comparisonProcessorService,
                                 VideoSnapRepository videoSnapRepository,
                                 SnapComparisonRepository snapComparisonRepository,
                                 OllamaVisionService ollamaVisionService) {
        this.comparisonProcessorService = comparisonProcessorService;
        this.snapComparisonRepository = snapComparisonRepository;
        this.videoSnapRepository = videoSnapRepository;
        this.ollamaVisionService = ollamaVisionService;
    }

    public SnapComparison compareVideoSnapsById(Long id1, Long id2) throws IOException {
        logger.info("compareVideoSnapsById, id {} to id {}", id1, id2);
        var snapSequence = getOrderedSnapSequence(id1, id2);
        ChatResponse chatResponse = ollamaVisionService.compareImages(
                PatternUtil.stripBase64DataUriPrefix(snapSequence.earlier.getData()),
                PatternUtil.stripBase64DataUriPrefix(snapSequence.later.getData()));

        var processedResponse = comparisonProcessorService.processComparisonResponse(snapSequence.earlier, snapSequence.later, chatResponse);

        processedResponse.forEach(r -> logger.info(r.toString()));
        List<String> comparison = processedResponse.stream().map(ComparisonResult::getResult).toList();

        var update = SnapComparison.builder().current(snapSequence.later).previous(snapSequence.earlier).comparison(comparison).build();
        snapComparisonRepository.save(update);

        return update;
    }

    private SnapSequence getOrderedSnapSequence(Long id1, Long id2) {
        VideoSnap snap1 = videoSnapRepository.findById(id1).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id1)));
        VideoSnap snap2 = videoSnapRepository.findById(id2).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id2)));

        var isSnap1Earlier = snap1.getDate().isBefore(snap2.getDate());

        return isSnap1Earlier ? new SnapSequence(snap1, snap2) : new SnapSequence(snap2, snap1);
    }

    private record SnapSequence(VideoSnap earlier, VideoSnap later) {
    }
}