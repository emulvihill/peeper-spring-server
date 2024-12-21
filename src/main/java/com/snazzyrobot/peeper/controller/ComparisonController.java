package com.snazzyrobot.peeper.controller;

import com.snazzyrobot.peeper.service.ComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class ComparisonController {

    private final ComparisonService comparisonService;

    @Autowired
    public ComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @QueryMapping
    public List<String> compareVideoSnapsById(@Argument Long id1, @Argument Long id2) throws IOException {
        return comparisonService.compareVideoSnapsById(id1, id2);
    }
}