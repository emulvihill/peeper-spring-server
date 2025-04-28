package com.snazzyrobot.peeper.repository;

import com.snazzyrobot.peeper.entity.CompareProfile;
import com.snazzyrobot.peeper.entity.PointOfInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Long> {
    List<PointOfInterest> findByCompareProfile(CompareProfile compareProfile);
    List<PointOfInterest> findByDetected(Boolean detected);
    List<PointOfInterest> findByCompareProfileAndDetected(CompareProfile compareProfile, Boolean detected);
}
