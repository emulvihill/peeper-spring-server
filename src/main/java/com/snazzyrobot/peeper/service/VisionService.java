package com.snazzyrobot.peeper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.snazzyrobot.peeper.dto.ComparisonFormat;
import com.snazzyrobot.peeper.entity.POIAction;
import com.snazzyrobot.peeper.entity.PointOfInterest;
import com.snazzyrobot.peeper.entity.PointOfInterestPOIAction;
import com.snazzyrobot.peeper.task.POIActionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

public abstract class VisionService {

    @Autowired
    @Qualifier("taskExecutor")
    protected Executor taskExecutor;

    protected final ChatModel chatModel;
    protected final UserMessageBuilder userMessageBuilder;
    protected final String modelName;

    public static final String USER_MESSAGE_BASE = """                        
            You are looking for the following list of comparisons:
            1. people entering or exiting the image.
            2. Items appearing or disappearing from the image.
            3. A person changing what they are holding, picking up or putting down objects.
            4. A person changing the activity they are performing.
            5. The color of each person's shirt.
            6. A yellow notepad with numbers written on it. If there is a yellow note with a number, tell me what the number is, and also solve the math problem "What is the square root of that number?"
            7. Count the number of persons (numPersons) which appear in the "after" image.
            
            When comparing images, do not worry about contrast or image orientation.
            Be concise in your descriptions.
            """;
    public static final String SYSTEM_MESSAGE_BASE = """
            You are a security expert. You are looking at two images, "before" and "after".
            """;

    public VisionService(ChatModel chatModel, UserMessageBuilder userMessageBuilder, String modelName) {
        this.chatModel = chatModel;
        this.userMessageBuilder = userMessageBuilder;
        this.modelName = modelName;
    }

    public Map.Entry<String, ComparisonFormat> compareImages(String before, String after, List<PointOfInterest> pointsOfInterest) throws IllegalArgumentException, IOException {

        var beanOutputConverter = new BeanOutputConverter<>(ComparisonFormat.class);

        ChatResponse response;

        ImageFileResource beforeResource = null;
        ImageFileResource afterResource = null;
        try {
            beforeResource = new ImageFileResource(before);
            afterResource = new ImageFileResource(after);
            SystemMessage systemMessage = new SystemMessage(SYSTEM_MESSAGE_BASE);

            // If no points of interest are provided, fetch them from the default profile
            List<PointOfInterest> poisToUse = pointsOfInterest;
            if (poisToUse == null || poisToUse.isEmpty()) {
                poisToUse = userMessageBuilder.getPointsOfInterestWithActions();
            }

            UserMessage userMessage = userMessageBuilder.createUserMessage(poisToUse, beforeResource, afterResource);

            logger.info("modelName: {}", modelName);
            logger.info("System Message: {}", systemMessage.getText());
            logger.info("User Message: {}", userMessage.getText());

            Prompt prompt = new Prompt(List.of(systemMessage, userMessage),
                    getChatOptions());

            response = chatModel.call(prompt);

        } finally {
            if (beforeResource != null) {
                beforeResource.cleanup();
            }
            if (afterResource != null) {
                afterResource.cleanup();
            }
        }

        Generation result = response.getResult();
        String responseText = result.getOutput().getText();
        ComparisonFormat converted = beanOutputConverter.convert(responseText);
        logger.info("Response: {}", responseText);
        assert converted != null;
        processDetectedPOI(pointsOfInterest, converted);

        return new AbstractMap.SimpleEntry<>(result.toString(), converted);
    }

    protected abstract ChatOptions getChatOptions() throws JsonProcessingException;

    protected static final Logger logger = LoggerFactory.getLogger(VisionService.class);

    String jsonSchema = """                
            {
              "$schema" : "https://json-schema.org/draft/2020-12/schema",
              "type": "object",
              "properties": {
                "numPersons": {
                  "type": "integer"
                },
                "comparisons": {
                  "type": "array",
                  "items": {
                    "type": "string"
                  }
                },
                "pointsOfInterest": {
                  "type": "array",
                  "items": {
                    "type": "string"
                  }
                }
              },
              "required": [
                "numPersons",
                "comparisons",
                "pointsOfInterest"
              ],
              "additionalProperties": false
            }
            """;

    protected void processDetectedPOI(List<PointOfInterest> pointsOfInterest, ComparisonFormat converted) {
        List<String> detectedPointsOfInterest = converted.getPointsOfInterest();

        // Process POIActions for detected PointOfInterest objects
        if (detectedPointsOfInterest != null && !detectedPointsOfInterest.isEmpty() && pointsOfInterest != null && !pointsOfInterest.isEmpty()) {
            for (PointOfInterest poi : pointsOfInterest) {
                // Check if this POI is mentioned in the detected list
                for (String detected : detectedPointsOfInterest) {
                    if (isSamePOI(poi, detected)) {
                        OllamaVisionService.logger.info("PointOfInterest detected: {}", poi.getRequest());
                        poi.setDetected(true);

                        // Process associated POIActions
                        Set<PointOfInterestPOIAction> poiActions = poi.getPointOfInterestPOIActions();
                        if (poiActions != null && !poiActions.isEmpty()) {
                            for (PointOfInterestPOIAction poiAction : poiActions) {
                                POIAction action = poiAction.getPoiAction();
                                if (action != null) {
                                    OllamaVisionService.logger.info("Executing POIAction: {}", action.getAction());
                                    taskExecutor.execute(new POIActionTask(action));
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private boolean isSamePOI(PointOfInterest poi, String detected) {
        // TODO: Call out to a similarity service here or use POI ids somehow
        return (poi.getRequest().contains("wearing a hat") && detected.contains("wearing a hat"));
    }
}
