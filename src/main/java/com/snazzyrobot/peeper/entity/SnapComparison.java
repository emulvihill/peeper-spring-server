package com.snazzyrobot.peeper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "snap_comparison")
public class SnapComparison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private VideoSnap current;

    @OneToOne
    private VideoSnap previous;

    @ElementCollection
    @CollectionTable(name = "snap_comparison_detections")
    @Column(name = "detection")
    private List<String> comparison;
}