package com.snazzyrobot.peeper.controller;

import com.snazzyrobot.peeper.entity.*;
import com.snazzyrobot.peeper.repository.CompareProfileRepository;
import com.snazzyrobot.peeper.repository.POIActionRepository;
import com.snazzyrobot.peeper.repository.PointOfInterestRepository;
import com.snazzyrobot.peeper.service.ComparisonService;
import com.snazzyrobot.peeper.service.VideoSnapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Controller
public class VideoSnapController {

    private static final Logger logger = LoggerFactory.getLogger(VideoSnapController.class);

    private final VideoSnapService videoSnapService;
    private final ComparisonService comparisonService;
    private final PointOfInterestRepository pointOfInterestRepository;
    private final POIActionRepository poiActionRepository;
    private final CompareProfileRepository compareProfileRepository;

    @Autowired
    public VideoSnapController(VideoSnapService videoSnapService, 
                              ComparisonService comparisonService,
                              PointOfInterestRepository pointOfInterestRepository,
                              POIActionRepository poiActionRepository,
                              CompareProfileRepository compareProfileRepository) {
        this.videoSnapService = videoSnapService;
        this.comparisonService = comparisonService;
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.poiActionRepository = poiActionRepository;
        this.compareProfileRepository = compareProfileRepository;
    }

    @QueryMapping
    public VideoSnap videoSnap(@Argument String id) {
        return videoSnapService.findById(Long.parseLong(id));
    }

    @QueryMapping
    public List<VideoSnap> videoSnaps() {
        return videoSnapService.list();
    }

    @QueryMapping
    public List<VideoSnap> videoSnapsForFeed(@Argument String feedId) {
        return videoSnapService.findAllForFeed(Long.parseLong(feedId));
    }

    @QueryMapping
    public List<SnapComparison> comparisonsForFeed(@Argument String feedId) {
        return comparisonService.findAllForFeed(Long.parseLong(feedId));
    }

    @MutationMapping
    public VideoSnap createVideoSnap(@Argument VideoSnapInput input) throws IOException {
        return videoSnapService.createVideoSnap(input);
    }

    @MutationMapping
    public SnapComparison createAndCompareVideoSnap(@Argument VideoSnapInput input) throws IOException {
        logger.info("Creating new video snap");
        return videoSnapService.createAndCompareVideoSnap(input);
    }

    @MutationMapping
    public Boolean deleteVideoSnap(@Argument Long id) {
        videoSnapService.deleteById(id);
        return true;
    }

    @MutationMapping
    public PointOfInterest createPointOfInterest(@Argument String profileId, @Argument String request) {
        logger.info("Creating new point of interest for profile ID: {} with request: {}", profileId, request);
        CompareProfile profile = compareProfileRepository.findById(Long.parseLong(profileId))
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with ID: " + profileId));
        
        PointOfInterest poi = new PointOfInterest();
        poi.setRequest(request);
        poi.setDetected(false);
        poi.setCompareProfile(profile);
        
        return pointOfInterestRepository.save(poi);
    }

    @MutationMapping
    public Boolean deletePointOfInterest(@Argument String id) {
        logger.info("Deleting point of interest with ID: {}", id);
        pointOfInterestRepository.deleteById(Long.parseLong(id));
        return true;
    }

    @MutationMapping
    @Transactional
    public POIAction createPOIAction(@Argument String poiId, @Argument String action) {
        logger.info("Creating new POI action for POI ID: {} with action: {}", poiId, action);
        PointOfInterest poi = pointOfInterestRepository.findById(Long.parseLong(poiId))
                .orElseThrow(() -> new IllegalArgumentException("Point of Interest not found with ID: " + poiId));
        
        POIAction poiAction = new POIAction();
        poiAction.setAction(action);
        poiAction.setCompareProfile(poi.getCompareProfile());
        
        POIAction savedAction = poiActionRepository.save(poiAction);
        
        // Add the POI to the action
        poi.getActions().add(poiAction);
        pointOfInterestRepository.save(poi);

        // Return the associated POIAction instance (now managed by persistence context)
        return poiAction;

    }

    @MutationMapping
    public Boolean deletePOIAction(@Argument String id) {
        logger.info("Deleting POI action with ID: {}", id);
        poiActionRepository.deleteById(Long.parseLong(id));
        return true;
    }
}