package com.snazzyrobot.peeper.repository;

import com.snazzyrobot.peeper.entity.POIAction;
import com.snazzyrobot.peeper.entity.PointOfInterest;
import com.snazzyrobot.peeper.entity.PointOfInterestPOIAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointOfInterestPOIActionRepository extends JpaRepository<PointOfInterestPOIAction, Long> {
    List<PointOfInterestPOIAction> findByPointOfInterest(PointOfInterest pointOfInterest);
    List<PointOfInterestPOIAction> findByPoiAction(POIAction poiAction);
    Optional<PointOfInterestPOIAction> findByPointOfInterestAndPoiAction(PointOfInterest pointOfInterest, POIAction poiAction);
}
