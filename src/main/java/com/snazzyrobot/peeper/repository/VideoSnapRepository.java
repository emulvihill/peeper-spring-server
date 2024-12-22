package com.snazzyrobot.peeper.repository;

import com.snazzyrobot.peeper.entity.Feed;
import com.snazzyrobot.peeper.entity.VideoSnap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoSnapRepository extends JpaRepository<VideoSnap, Long> {
    List<VideoSnap> findByFeed(Optional<Feed> byId);

    Optional<VideoSnap> findTopByOrderByCreatedDesc();

    VideoSnap findFirstByIdLessThanAndFeedOrderByIdDesc(Long id, Feed feed);
}