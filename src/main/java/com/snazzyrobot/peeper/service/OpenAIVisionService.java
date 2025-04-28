package com.snazzyrobot.peeper.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Qualifier;

public class OpenAIVisionService extends VisionService {

    public OpenAIVisionService(@Qualifier("openAiChatModel") ChatModel chatModel, UserMessageBuilder userMessageBuilder, String modelName) {
        super(chatModel, userMessageBuilder, modelName);
    }

    protected OpenAiChatOptions getChatOptions() {
        return OpenAiChatOptions.builder()
                .model(modelName)
                .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                .build();
    }
}
