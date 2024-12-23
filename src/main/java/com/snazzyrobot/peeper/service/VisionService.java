package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.utility.ImageUtil;
import lib.ASCII;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public abstract class VisionService {
    abstract List<String> compareImages(String before, String after) throws IOException;

    private static final Logger logger = LoggerFactory.getLogger(VisionService.class);

    String jsonSchema = """                
                {
                  "$schema": "http://json-schema.org/draft-04/schema#",
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
                    }
                  },
                  "required": [
                    "numPersons",
                    "comparisons"
                  ],
                  "additionalProperties": false
                }
                """;

    File createTempImageFile(BufferedImage image) throws IOException {
        File tempFile = Files.createTempFile(null, ".png").toFile();
        ImageIO.write(image, "png", tempFile);
        return tempFile;
    }

    void displayImagesInConsole(String before, String after) {
        ASCII ascii = new ASCII(false, 2, 3);
        try {
            var img1 = ImageUtil.decodeBase64ToImage(before);
            var img2 = ImageUtil.decodeBase64ToImage(after);
            logger.info(ascii.convert(img1));
            logger.info(ascii.convert(img2));
        } catch (IOException e) {
            logger.info("Could not display images as ASCII");
        }
    }
}
