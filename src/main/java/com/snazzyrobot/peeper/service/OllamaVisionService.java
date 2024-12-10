package com.snazzyrobot.peeper.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OllamaVisionService {

    private final String modelName;
    private final OllamaChatModel chatModel;

    public OllamaVisionService(OllamaChatModel chatModel, @Value("${OLLAMA_MODEL:llama3.2-vision}") String modelName) {
        this.chatModel = chatModel;
        this.modelName = modelName;
    }

    public String askQuestion(String question) {
        ChatResponse response = chatModel.call(
                new Prompt(question));

        return response.toString();
    }

    public String compareImages(String left, String right) {
        return askQuestion("What color is the sky?");
    }
}