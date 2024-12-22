package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.SnapComparison;
import com.snazzyrobot.peeper.entity.VideoSnap;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class AsyncComparisonService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncComparisonService.class);

    private final ComparisonService comparisonService;
    private final VideoSnapRepository videoSnapRepository;

    public AsyncComparisonService(ComparisonService comparisonService,
                                  VideoSnapRepository videoSnapRepository) {
        this.comparisonService = comparisonService;
        this.videoSnapRepository = videoSnapRepository;
    }

    @Async("comparisonTaskExecutor")
    @Transactional
    @Timed
    public void compareWithPreviousSnap(Long currentSnapId) {
        try {
            VideoSnap prevSnap = videoSnapRepository.findTopByIdOrderByDateDesc(currentSnapId)
                    .orElse(null);

            if (prevSnap != null) {
                SnapComparison comparison = comparisonService.compareVideoSnapsById(prevSnap.getId(), currentSnapId);
                logger.info("Comparison completed for snap ID: {} with previous snap ID: {}, Comparison: {}",
                        currentSnapId, prevSnap.getId(), comparison.getComparison());
            } else {
                logger.info("No previous snap found for comparison with ID: {}", currentSnapId);
            }
        } catch (IOException e) {
            logger.error("Error comparing video snaps. Current ID: {}", currentSnapId, e);
        }
    }
}