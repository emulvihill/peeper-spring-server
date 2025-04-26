package com.snazzyrobot.peeper.repository;

import com.snazzyrobot.peeper.entity.CompareProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompareProfileRepository extends JpaRepository<CompareProfile, Long> {
    Optional<CompareProfile> findByName(String name);
}