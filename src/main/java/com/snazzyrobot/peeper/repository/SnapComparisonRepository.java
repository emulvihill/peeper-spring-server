package com.snazzyrobot.peeper.repository;

import com.snazzyrobot.peeper.entity.SnapComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SnapComparisonRepository extends JpaRepository<SnapComparison, Long> {

}