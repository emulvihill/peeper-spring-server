package com.snazzyrobot.peeper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snazzyrobot.peeper.dto.ComparisonFormat;
import com.snazzyrobot.peeper.entity.PointOfInterest;
import com.snazzyrobot.peeper.utility.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaVisionService extends VisionService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaVisionService.class);

    private final String modelName;
    private final OllamaChatModel chatModel;

    public OllamaVisionService(OllamaChatModel chatModel, @Value("${OLLAMA_MODEL:llama3.2:latest}") String modelName) {
        this.chatModel = chatModel;
        this.modelName = modelName;
    }

    @Override
    public Map.Entry<String, ComparisonFormat> compareImages(String before, String after) throws IOException {
        return compareImages(before, after, null);
    }

    @Override
    public Map.Entry<String, ComparisonFormat> compareImages(String before, String after, List<PointOfInterest> pointsOfInterest) throws IOException {

        var beanOutputConverter = new BeanOutputConverter<>(ComparisonFormat.class);
        String format = beanOutputConverter.getFormat();

        File beforeTmp = null, afterTmp = null;
        ChatResponse response;

        try {
            var beforeImg = ImageUtil.decodeBase64ToImage(before);
            beforeTmp = createTempImageFile(beforeImg);
            var beforeResource = new PathResource(beforeTmp.getAbsolutePath());

            var afterImg = ImageUtil.decodeBase64ToImage(after);
            afterTmp = createTempImageFile(afterImg);
            var afterResource = new PathResource(afterTmp.getAbsolutePath());

            SystemMessage systemMessage = new SystemMessage("""
                    You are a security expert. You are looking at two images, "before" and "after".
                    """);

            StringBuilder userMessageText = new StringBuilder("""                        
                    You are looking for the following list of comparisons:
                    1. people entering or exiting the image.
                    2. Items appearing or disappearing from the image.
                    3. A person changing what they are holding, picking up or putting down objects.
                    4. A person changing the activity they are performing.
                    5. A yellow notepad with numbers written on it. If there is a yellow note with a number,
                    include the following in your response: "Hi ho, hi ho, it's off to work we go. The number I see is (the number)"

                    Also count the number of persons (numPersons) which appear in the "after" image.

                    When comparing images, do not worry about contrast or image orientation.
                    Be concise in your descriptions.
                    """);

            // Add points of interest if provided
            if (pointsOfInterest != null) {
                pointsOfInterest.forEach (poi -> {
                    userMessageText.append("\n\nAdditionally, check for the following point of interest: ")
                            .append(poi.getRequest())
                            .append("\nIf found, include details in the pointOfInterestResponse field.");
                });
            }

            UserMessage userMessage = new UserMessage(userMessageText.toString(),
                    Media.builder().mimeType(MimeTypeUtils.IMAGE_PNG).data(beforeResource).name("before").build(),
                    Media.builder().mimeType(MimeTypeUtils.IMAGE_PNG).data(afterResource).name("after").build()
                    );

            Prompt prompt = new Prompt(List.of(systemMessage, userMessage),
                    OllamaOptions.builder()
                            .model(modelName)
                            .format(new ObjectMapper().readValue(jsonSchema, Map.class))
                            .build());

            response = chatModel.call(prompt);

        } finally {
            if (beforeTmp != null) {
                beforeTmp.delete();
            }
            if (afterTmp != null) {
                afterTmp.delete();
            }
        }

        ComparisonFormat converted = beanOutputConverter.convert(response.getResult().getOutput().getText());
        return new AbstractMap.SimpleEntry<>(response.getResult().toString(), converted);
    }
}
