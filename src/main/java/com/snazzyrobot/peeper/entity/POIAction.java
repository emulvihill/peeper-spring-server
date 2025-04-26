package com.snazzyrobot.peeper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Getter
@Setter
@Entity(name = "POIAction")
@Table(name = "poi_action")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class POIAction extends BaseEntity implements EntityDetails {

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
    private String action;

    @ManyToOne
    @JoinColumn(name = "compare_profile_id")
    private CompareProfile compareProfile;

    @OneToMany(mappedBy = "poiAction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PointOfInterestPOIAction> pointOfInterestPOIActions = new HashSet<>();

    public void addPointOfInterest(PointOfInterest pointOfInterest) {
        PointOfInterestPOIAction pointOfInterestPOIAction = new PointOfInterestPOIAction();
        pointOfInterestPOIAction.setPointOfInterest(pointOfInterest);
        pointOfInterestPOIAction.setPoiAction(this);
        pointOfInterestPOIActions.add(pointOfInterestPOIAction);
        pointOfInterest.getPointOfInterestPOIActions().add(pointOfInterestPOIAction);
    }

    public void removePointOfInterest(PointOfInterest pointOfInterest) {
        for (Iterator<PointOfInterestPOIAction> iterator = pointOfInterestPOIActions.iterator(); iterator.hasNext();) {
            PointOfInterestPOIAction pointOfInterestPOIAction = iterator.next();

            if (pointOfInterestPOIAction.getPoiAction().equals(this) &&
                    pointOfInterestPOIAction.getPointOfInterest().equals(pointOfInterest)) {
                iterator.remove();
                pointOfInterestPOIAction.getPointOfInterest().getPointOfInterestPOIActions().remove(pointOfInterestPOIAction);
                pointOfInterestPOIAction.setPoiAction(null);
                pointOfInterestPOIAction.setPointOfInterest(null);
            }
        }
    }
}
