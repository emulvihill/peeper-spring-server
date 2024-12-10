package com.snazzyrobot.peeper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity(name = "VideoSnap")
@Table(name = "video_snap")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoSnap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "feed_id", referencedColumnName = "id")
    private Feed feed;

    @Column(nullable = false)
    private OffsetDateTime date;

    @Column(nullable = false)
    private String data;
}