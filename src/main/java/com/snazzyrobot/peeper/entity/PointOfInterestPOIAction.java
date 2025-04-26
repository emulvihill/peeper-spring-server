package com.snazzyrobot.peeper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity(name = "PointOfInterestPOIAction")
@Table(name = "point_of_interest_poi_action")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PointOfInterestPOIAction extends BaseEntity implements EntityDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "created", nullable = false, updatable = false)
    @CreatedDate
    @ToString.Exclude
    private OffsetDateTime created;

    @Column(name = "modified", nullable = false)
    @LastModifiedDate
    @ToString.Exclude
    private OffsetDateTime modified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_of_interest_id", nullable = false)
    private PointOfInterest pointOfInterest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poi_action_id", nullable = false)
    private POIAction poiAction;
}