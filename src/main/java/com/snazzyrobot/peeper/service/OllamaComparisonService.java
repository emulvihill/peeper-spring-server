package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import com.snazzyrobot.peeper.utility.PatternUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OllamaComparisonService implements ComparisonService {

    private static final String VIDEO_SNAP_NOT_FOUND = "VideoSnap with id %d not found";

    private final VideoSnapRepository videoSnapRepository;
    private final OllamaVisionService ollamaVisionService;

    public OllamaComparisonService(VideoSnapRepository videoSnapRepository, OllamaVisionService ollamaVisionService) {
        this.videoSnapRepository = videoSnapRepository;
        this.ollamaVisionService = ollamaVisionService;
    }

    public String compareVideoSnapsById(Long id1, Long id2) throws IOException {
        var snapData = getOrderedVideoSnapData(id1, id2);
        return ollamaVisionService.compareImages(PatternUtil.stripBase64DataUriPrefix(snapData.earlierData()), PatternUtil.stripBase64DataUriPrefix(snapData.laterData()));
    }

    private SnapData getOrderedVideoSnapData(Long id1, Long id2) {
        VideoSnap snap1 = videoSnapRepository.findById(id1).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id1)));
        VideoSnap snap2 = videoSnapRepository.findById(id2).orElseThrow(() -> new IllegalArgumentException(String.format(VIDEO_SNAP_NOT_FOUND, id2)));

        var isSnap1Earlier = snap1.getDate().isBefore(snap2.getDate());
        String earlierData = isSnap1Earlier ? snap1.getData() : snap2.getData();
        String laterData = isSnap1Earlier ? snap2.getData() : snap1.getData();

        return new SnapData(earlierData, laterData);
    }

    private record SnapData(String earlierData, String laterData) {
    }
}