package com.snazzyrobot.peeper.controller;

import com.snazzyrobot.peeper.entity.SnapComparison;
import com.snazzyrobot.peeper.service.ComparisonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class ComparisonController {

    private static final Logger logger = LoggerFactory.getLogger(ComparisonController.class);

    private final ComparisonService comparisonService;

    @Autowired
    public ComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @QueryMapping
    public SnapComparison compareVideoSnapsById(@Argument Long id1, @Argument Long id2, @Argument String profile) throws IOException {
        logger.info("Comparing video snaps with IDs: {} and {}, profile: {}", id1, id2, profile);

        return comparisonService.compareVideoSnapsById(id1, id2, profile);
    }
}
