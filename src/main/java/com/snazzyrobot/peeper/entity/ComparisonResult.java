package com.snazzyrobot.peeper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity(name = "ComparisonResult")
@Table(name = "comparison_result")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComparisonResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique=true, nullable = false)
    private String responseId;

    @OneToOne
    @JoinColumn(name = "before_id", referencedColumnName = "id")
    private VideoSnap before;

    @OneToOne
    @JoinColumn(name = "after_id", referencedColumnName = "id")
    private VideoSnap after;

    @Column(nullable = false)
    private OffsetDateTime date;

    @Column(nullable = false)
    private String result;

    @Column(nullable = false)
    private boolean resultDetected;
}