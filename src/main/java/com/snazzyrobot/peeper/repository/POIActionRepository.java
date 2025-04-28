package com.snazzyrobot.peeper.repository;

import com.snazzyrobot.peeper.entity.CompareProfile;
import com.snazzyrobot.peeper.entity.POIAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface POIActionRepository extends JpaRepository<POIAction, Long> {
    List<POIAction> findByCompareProfile(CompareProfile compareProfile);
    Optional<POIAction> findByAction(String action);
}
