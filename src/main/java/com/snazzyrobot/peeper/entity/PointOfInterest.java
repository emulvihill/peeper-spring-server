package com.snazzyrobot.peeper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity(name = "PointOfInterest")
@Table(name = "point_of_interest")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PointOfInterest extends BaseEntity implements EntityDetails {

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

    @Column(nullable = false)
    private String request;

    @Column(nullable = false)
    private Boolean detected;

    @ManyToOne
    @JoinColumn(name = "compare_profile_id")
    private CompareProfile compareProfile;

    @OneToMany(mappedBy = "pointOfInterest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PointOfInterestPOIAction> pointOfInterestPOIActions = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "point_of_interest_poi_action", joinColumns = {@JoinColumn(name = "point_of_interest_id")}, inverseJoinColumns = {@JoinColumn(name = "poi_action_id")})
    private List<POIAction> actions = new ArrayList<>(0);
}
