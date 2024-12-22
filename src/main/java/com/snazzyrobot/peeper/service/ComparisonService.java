package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.SnapComparison;

import java.io.IOException;

public interface ComparisonService {
    SnapComparison compareVideoSnapsById(Long id1, Long id2) throws IOException;
}
