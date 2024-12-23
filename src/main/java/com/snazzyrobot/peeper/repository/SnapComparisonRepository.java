package com.snazzyrobot.peeper.repository;

import com.snazzyrobot.peeper.entity.Feed;
import com.snazzyrobot.peeper.entity.SnapComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SnapComparisonRepository extends JpaRepository<SnapComparison, Long> {

    List<SnapComparison> findAllByFeed(Optional<Feed> byId);
}