package com.snazzyrobot.peeper.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService {

    private final ChatClient chatClient;
    private final String modelName;

    public OpenAIService(OpenAiChatModel chatModel, @Value("${OPENAI_MODEL:gpt-40}") String modelName) {
        this.modelName = modelName;
        this.chatClient = ChatClient.create(chatModel);
    }

    public String askQuestion(String question) {
        return chatClient.prompt(modelName)
                .user(question)
                .call()
                .content();
    }
}