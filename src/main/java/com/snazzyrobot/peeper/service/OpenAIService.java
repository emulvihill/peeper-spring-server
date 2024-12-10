package com.snazzyrobot.peeper.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAIService {

    private final ChatClient chatClient;
    private final String openaiModel;

    public OpenAIService(ChatClient chatClient, @Value("${OPENAI_MODEL:gpt-40}") String openaiModel) {
        this.chatClient = chatClient;
        this.openaiModel = openaiModel;
    }

    public String askQuestion(String question) {
        return chatClient.prompt(openaiModel)
                .user(question)
                .call()
                .content();
    }
}