package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.entity.CompareProfile;
import com.snazzyrobot.peeper.entity.POIAction;
import com.snazzyrobot.peeper.entity.PointOfInterest;
import com.snazzyrobot.peeper.repository.CompareProfileRepository;
import com.snazzyrobot.peeper.repository.POIActionRepository;
import com.snazzyrobot.peeper.repository.PointOfInterestPOIActionRepository;
import com.snazzyrobot.peeper.repository.PointOfInterestRepository;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserMessageBuilder {
    private final CompareProfileRepository compareProfileRepository;
    private final PointOfInterestRepository pointOfInterestRepository;
    private final POIActionRepository poiActionRepository;
    private final PointOfInterestPOIActionRepository pointOfInterestPOIActionRepository;

    @Autowired
    public UserMessageBuilder(CompareProfileRepository compareProfileRepository,
                             PointOfInterestRepository pointOfInterestRepository,
                             POIActionRepository poiActionRepository,
                             PointOfInterestPOIActionRepository pointOfInterestPOIActionRepository) {
        this.compareProfileRepository = compareProfileRepository;
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.poiActionRepository = poiActionRepository;
        this.pointOfInterestPOIActionRepository = pointOfInterestPOIActionRepository;
    }

    /**
     * Fetches all PointOfInterest and POIAction for the default CompareProfile,
     * and arranges them by PointOfInterest according to their relationship in the PointOfInterestPOIAction join table.
     *
     * @return A list of PointOfInterest with their associated POIActions
     */
    public List<PointOfInterest> getPointsOfInterestWithActions() {
        // Get the default CompareProfile
        Optional<CompareProfile> defaultProfileOpt = compareProfileRepository.findByName("default");
        if (defaultProfileOpt.isEmpty()) {
            return Collections.emptyList();
        }

        CompareProfile compareProfile = defaultProfileOpt.get();

        // Get all PointOfInterest for the default profile
        List<PointOfInterest> pointsOfInterest = pointOfInterestRepository.findByCompareProfile(compareProfile);

        // Get all POIAction for the default profile
        List<POIAction> poiActions = poiActionRepository.findByCompareProfile(compareProfile);

        // For each PointOfInterest, get its associated POIActions through the join table
/*        for (PointOfInterest poi : pointsOfInterest) {
            List<PointOfInterestPOIAction> joinEntries = pointOfInterestPOIActionRepository.findByPointOfInterest(poi);

            // Clear existing actions and add the ones from the join table
            poi.getPointOfInterestPOIActions().clear();
            for (PointOfInterestPOIAction joinEntry : joinEntries) {
                poi.getPointOfInterestPOIActions().add(joinEntry);
            }
        }*/

        return pointsOfInterest;
    }

    UserMessage createUserMessage(List<PointOfInterest> pointsOfInterest,
                                  ImageFileResource beforeResource,
                                  ImageFileResource afterResource) {
        StringBuilder userText = new StringBuilder(OllamaVisionService.USER_MESSAGE_BASE);

        // Add points of interest if provided
        if (pointsOfInterest != null) {
            pointsOfInterest.forEach(poi -> {
                userText.append("\n\nAdditionally, check for the following point of interest: ")
                        .append(poi.getRequest())
                        .append("\nIf found, include details in the pointOfInterestResponse field.");
            });
        }

        return new UserMessage(userText.toString(),
                Media.builder().mimeType(MimeTypeUtils.IMAGE_PNG).data(beforeResource.getPathResource()).name("before").build(),
                Media.builder().mimeType(MimeTypeUtils.IMAGE_PNG).data(afterResource.getPathResource()).name("after").build());
    }
}
