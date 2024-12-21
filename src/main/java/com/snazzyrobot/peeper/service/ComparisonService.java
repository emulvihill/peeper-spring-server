package com.snazzyrobot.peeper.service;

import java.io.IOException;
import java.util.List;

public interface ComparisonService {
    List<String> compareVideoSnapsById(Long id1, Long id2) throws IOException;
}
