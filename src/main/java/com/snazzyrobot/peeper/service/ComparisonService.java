package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.dto.VideoUpdate;

import java.io.IOException;

public interface ComparisonService {
    VideoUpdate compareVideoSnapsById(Long id1, Long id2) throws IOException;
}
