package com.snazzyrobot.peeper.repository;

import com.snazzyrobot.peeper.entity.ComparisonResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComparisonResultRepository extends JpaRepository<ComparisonResult, Long> {
    List<ComparisonResult> findByBeforeAfter(Optional<ComparisonResult> byId);

    Optional<ComparisonResult> findTopByOrderByIdDesc();

    Optional<ComparisonResult> findTopByOrderByDateDesc();
}