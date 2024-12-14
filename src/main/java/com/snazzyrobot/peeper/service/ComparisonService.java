package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import com.snazzyrobot.peeper.utility.PatternUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ComparisonService {

    private final VideoSnapRepository videoSnapRepository;
    private final OllamaVisionService ollamaVisionService;

    public ComparisonService(VideoSnapRepository videoSnapRepository, OllamaVisionService ollamaVisionService) {
        this.videoSnapRepository = videoSnapRepository;
        this.ollamaVisionService = ollamaVisionService;
    }

    public String compareVideoSnapsById(Long id1, Long id2) throws IOException {
        VideoSnap snap1 = videoSnapRepository.findById(id1).orElseThrow(() -> new IllegalArgumentException("VideoSnap with id " + id1 + " not found"));
        VideoSnap snap2 = videoSnapRepository.findById(id2).orElseThrow(() -> new IllegalArgumentException("VideoSnap with id " + id2 + " not found"));

        var id1Earlier = snap1.getDate().isBefore(snap2.getDate());
        String before = id1Earlier ? snap1.getData() : snap2.getData();
        String after = id1Earlier ? snap2.getData() : snap1.getData();

        return ollamaVisionService.compareImagesUsingCombining(
                PatternUtil.stripBase64DataUriPrefix(before),
                PatternUtil.stripBase64DataUriPrefix(after));
    }
}