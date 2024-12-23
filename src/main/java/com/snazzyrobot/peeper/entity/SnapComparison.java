package com.snazzyrobot.peeper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "snap_comparison")
@EntityListeners(AuditingEntityListener.class)
public class SnapComparison extends BaseEntity implements EntityDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    @ToString.Exclude
    private OffsetDateTime created;

    @Column(name = "modified", nullable = false)
    @LastModifiedDate
    @ToString.Exclude
    private OffsetDateTime modified;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "feed_id", referencedColumnName = "id")
    private Feed feed;

    @OneToOne
    private VideoSnap current;

    @OneToOne
    private VideoSnap previous;

    @ElementCollection
    @CollectionTable(name = "snap_comparison_detections")
    @Column(name = "detection")
    private List<String> comparison;

    @Column(nullable = false)
    String rawComparison;

    @Column(nullable = false)
    private boolean resultDetected;
}