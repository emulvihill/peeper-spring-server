package com.snazzyrobot.peeper.task;

import com.snazzyrobot.peeper.entity.POIAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task to process a POIAction when a PointOfInterest is detected.
 * This task is executed by a TaskExecutor.
 */
public class POIActionTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(POIActionTask.class);
    
    private final POIAction poiAction;
    
    public POIActionTask(POIAction poiAction) {
        this.poiAction = poiAction;
    }
    
    @Override
    public void run() {
        try {
            logger.info("Processing POIAction: {}", poiAction.getAction());
            // Add actual processing logic here
            // This could involve calling other services, sending notifications, etc.
        } catch (Exception e) {
            logger.error("Error processing POIAction: {}", poiAction.getAction(), e);
        }
    }
}