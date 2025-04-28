package com.snazzyrobot.peeper.service;

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
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIVisionService extends VisionService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIVisionService.class);

    private final String modelName;
    private final OpenAiChatModel chatModel;
    private final UserMessageBuilder userMessageBuilder;

    public OpenAIVisionService(OpenAiChatModel chatModel,
                               UserMessageBuilder userMessageBuilder,
                               @Value("${OPENAI_MODEL:gpt-4o-mini}") String modelName) {
        this.chatModel = chatModel;
        this.userMessageBuilder = userMessageBuilder;
        this.modelName = modelName;
    }

    @Override
    public Map.Entry<String, ComparisonFormat> compareImages(String before, String after, List<PointOfInterest> pointsOfInterest) throws IllegalArgumentException, IOException {

        var beanOutputConverter = new BeanOutputConverter<>(ComparisonFormat.class);

        File beforeTmp = null, afterTmp = null;
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
                    OpenAiChatOptions.builder()
                            .model(modelName)
                            .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
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
        return new AbstractMap.SimpleEntry<>(result.toString(), converted);
    }
}
