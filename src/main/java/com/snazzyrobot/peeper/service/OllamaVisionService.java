package com.snazzyrobot.peeper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snazzyrobot.peeper.dto.ComparisonFormat;
import com.snazzyrobot.peeper.entity.PointOfInterest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaVisionService extends VisionService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaVisionService.class);

    private final String modelName;
    private final OllamaChatModel chatModel;
    private final UserMessageBuilder userMessageBuilder;

    public OllamaVisionService(OllamaChatModel chatModel,
                               UserMessageBuilder userMessageBuilder,
                               @Value("${OLLAMA_MODEL:llama3.2:latest}") String modelName) {
        this.chatModel = chatModel;
        this.userMessageBuilder = userMessageBuilder;
        this.modelName = modelName;
    }

    @Override
    public Map.Entry<String, ComparisonFormat> compareImages(String before, String after, List<PointOfInterest> pointsOfInterest) throws IOException {

        var beanOutputConverter = new BeanOutputConverter<>(ComparisonFormat.class);
        String format = beanOutputConverter.getFormat();

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
                    OllamaOptions.builder()
                            .model(modelName)
                            .format(new ObjectMapper().readValue(jsonSchema, Map.class))
                            .build());
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
        return new AbstractMap.SimpleEntry<>(response.getResult().toString(), converted);
    }
}
