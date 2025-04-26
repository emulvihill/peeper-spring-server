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

    public void addPOIAction(POIAction poiAction) {
        PointOfInterestPOIAction pointOfInterestPOIAction = new PointOfInterestPOIAction();
        pointOfInterestPOIAction.setPointOfInterest(this);
        pointOfInterestPOIAction.setPoiAction(poiAction);
        pointOfInterestPOIActions.add(pointOfInterestPOIAction);
        poiAction.getPointOfInterestPOIActions().add(pointOfInterestPOIAction);
    }

    public void removePOIAction(POIAction poiAction) {
        for (Iterator<PointOfInterestPOIAction> iterator = pointOfInterestPOIActions.iterator(); iterator.hasNext();) {
            PointOfInterestPOIAction pointOfInterestPOIAction = iterator.next();

            if (pointOfInterestPOIAction.getPointOfInterest().equals(this) &&
                    pointOfInterestPOIAction.getPoiAction().equals(poiAction)) {
                iterator.remove();
                pointOfInterestPOIAction.getPoiAction().getPointOfInterestPOIActions().remove(pointOfInterestPOIAction);
                pointOfInterestPOIAction.setPointOfInterest(null);
                pointOfInterestPOIAction.setPoiAction(null);
            }
        }
    }
}
