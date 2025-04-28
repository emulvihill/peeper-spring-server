package com.snazzyrobot.peeper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

public class OllamaVisionService extends VisionService {

    public OllamaVisionService(@Qualifier("ollamaChatModel") ChatModel chatModel, UserMessageBuilder userMessageBuilder, String modelName) {
        super(chatModel, userMessageBuilder, modelName);
    }

    protected OllamaOptions getChatOptions() throws JsonProcessingException {
        return OllamaOptions.builder()
                .model(modelName)
                .format(new ObjectMapper().readValue(jsonSchema, Map.class))
                .build();
    }

}
