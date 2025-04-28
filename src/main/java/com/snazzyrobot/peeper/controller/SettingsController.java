package com.snazzyrobot.peeper.controller;

import com.snazzyrobot.peeper.entity.CompareProfile;
import com.snazzyrobot.peeper.entity.PointOfInterest;
import com.snazzyrobot.peeper.repository.CompareProfileRepository;
import com.snazzyrobot.peeper.repository.PointOfInterestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    private final CompareProfileRepository compareProfileRepository;
    private final PointOfInterestRepository pointOfInterestRepository;

    @Autowired
    public SettingsController(CompareProfileRepository compareProfileRepository,
                             PointOfInterestRepository pointOfInterestRepository) {
        this.compareProfileRepository = compareProfileRepository;
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    @QueryMapping
    public List<CompareProfile> compareProfiles() {
        logger.info("Fetching all compare profiles");
        return compareProfileRepository.findAll();
    }

    @QueryMapping
    public List<PointOfInterest> pointsOfInterestForProfile(@Argument Long profileId) {
        logger.info("Fetching points of interest for profile ID: {}", profileId);
        CompareProfile profile = compareProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));
        return pointOfInterestRepository.findByCompareProfile(profile);
    }
}