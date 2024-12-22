package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.ComparisonResult;
import com.snazzyrobot.peeper.entity.SnapComparison;
import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.repository.SnapComparisonRepository;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import com.snazzyrobot.peeper.utility.PatternUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class OpenAIComparisonService implements ComparisonService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIComparisonService.class);

    private static final String VIDEO_SNAP_NOT_FOUND = "VideoSnap with id %d not found";

    private final VideoSnapRepository videoSnapRepository;
    private final SnapComparisonRepository snapComparisonRepository;
    private final OpenAIVisionService openAIService;
    private final ComparisonProcessorService comparisonProcessorService;

    public OpenAIComparisonService(ComparisonProcessorService comparisonProcessorService,
                                   VideoSnapRepository videoSnapRepository,
                                   SnapComparisonRepository snapComparisonRepository,
                                   OpenAIVisionService openAIVisionService) {
        this.comparisonProcessorService = comparisonProcessorService;
        this.videoSnapRepository = videoSnapRepository;
        this.snapComparisonRepository = snapComparisonRepository;
        this.openAIService = openAIVisionService;
    }

    public SnapComparison compareVideoSnapsById(Long id1, Long id2) throws IOException {
        logger.info("compareVideoSnapsById, id {} to id {}", id1, id2);

        VideoSnap snap1 = videoSnapRepository.findById(id1).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id1)));
        VideoSnap snap2 = videoSnapRepository.findById(id2).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id2)));

        var isSnap1Earlier = snap1.getDate().isBefore(snap2.getDate());
        VideoSnap before = isSnap1Earlier ? snap1 : snap2;
        VideoSnap after = isSnap1Earlier ? snap2 : snap1;

        var response = openAIService.compareImages(PatternUtil.stripBase64DataUriPrefix(before.getData()), PatternUtil.stripBase64DataUriPrefix(after.getData()));

        var processedResponse = comparisonProcessorService.processComparisonResponse(before, after, response);

        processedResponse.forEach(r -> logger.info(r.toString()));
        List<String> comparison = processedResponse.stream().map(ComparisonResult::getResult).toList();

        var update = SnapComparison.builder().current(after).previous(before).comparison(comparison).build();
        snapComparisonRepository.save(update);

        return update;
    }
}