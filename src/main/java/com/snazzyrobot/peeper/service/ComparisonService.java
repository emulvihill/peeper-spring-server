package com.snazzyrobot.peeper.service;

import java.io.IOException;

public interface ComparisonService {
    String compareVideoSnapsById(Long id1, Long id2) throws IOException;
}
