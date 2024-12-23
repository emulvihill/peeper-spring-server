package com.snazzyrobot.peeper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snazzyrobot.peeper.utility.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIVisionService extends VisionService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIVisionService.class);

    private final String modelName;
    private final OpenAiChatModel chatModel;

    public OpenAIVisionService(OpenAiChatModel chatModel, @Value("${OPENAI_MODEL:gpt-4o-mini}") String modelName) {
        this.chatModel = chatModel;
        this.modelName = modelName;
    }

    public List<String> compareImages(String before, String after) throws IllegalArgumentException, IOException {

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

            UserMessage userMessage = new UserMessage("""                        
                    You are looking for the following list of comparisons:
                    1. people entering or exiting the image.
                    2. Items appearing or disappearing from the image.
                    3. A person changing what they are holding, picking up or putting down objects.
                    4. A person changing the activity they are performing.
                    
                    Also count the number of persons (numPersons) which appear in the "after" image.
                    
                    When comparing images, do not worry about contrast or image orientation.
                    Be concise in your descriptions.
                    """, new Media(MimeTypeUtils.IMAGE_PNG, beforeResource), new Media(MimeTypeUtils.IMAGE_PNG, afterResource));

            Prompt prompt = new Prompt(List.of(systemMessage, userMessage),
                    OpenAiChatOptions.builder()
                            .model(modelName)
                            .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
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

        return response.getResults().stream().map(result -> result.getOutput().getContent()).toList();
    }

    private ChatOptions getChatOptions() throws JsonProcessingException {
        return OllamaOptions.builder()
                .model(modelName)
                .format(new ObjectMapper().readValue(jsonSchema, Map.class))
                .build();
    }
}