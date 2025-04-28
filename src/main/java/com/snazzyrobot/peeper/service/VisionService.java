package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.dto.ComparisonFormat;
import com.snazzyrobot.peeper.entity.PointOfInterest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class VisionService {
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

    abstract Map.Entry<String, ComparisonFormat> compareImages(String before, String after, List<PointOfInterest> pointsOfInterest) throws IOException;

    private static final Logger logger = LoggerFactory.getLogger(VisionService.class);

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
                "pointOfInterestResponse": {
                  "type": "string"
                }
              },
              "required": [
                "numPersons",
                "comparisons",
                "pointOfInterestResponse"
              ],
              "additionalProperties": false
            }
            """;
}
