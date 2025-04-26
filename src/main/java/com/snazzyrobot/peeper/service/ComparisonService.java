package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.SnapComparison;

import java.io.IOException;
import java.util.List;

public interface ComparisonService {
    SnapComparison compareVideoSnapsById(Long id1, Long id2, String profile) throws IOException;

    List<SnapComparison> findAllForFeed(long l);
}
