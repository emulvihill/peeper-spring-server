package com.snazzyrobot.peeper.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.ChatRequest;
import org.springframework.ai.ollama.api.OllamaApi.Message;
import org.springframework.ai.ollama.api.OllamaApi.Message.Role;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OllamaVisionService {

    private final String modelName;
    private final OllamaApi ollamaApi;
    private final OllamaChatModel chatModel;

    public OllamaVisionService(OllamaApi ollamaApi, OllamaChatModel chatModel, @Value("${OLLAMA_MODEL:llama3.2-vision}") String modelName) {
        this.ollamaApi = ollamaApi;
        this.chatModel = chatModel;
        this.modelName = modelName;
    }

    public String askQuestion(String question) {
        ChatResponse response = chatModel.call(new Prompt(question));
        return response.toString();
    }

    public String compareImages(String before, String after) {

        var request = ChatRequest.builder(modelName)
                .withStream(false) // not streaming
                .withMessages(List.of(
                        Message.builder(Role.SYSTEM)
                                .withContent("You are a security monitor. You are looking for anything unusual.")
                                .build(),
                        Message.builder(Role.USER)
                                .withContent("Compare these two images closely. What, if anything, is different between these two images?")
                                //.withContent("What is in this image?")
                                .withImages(List.of(before, after))
                                .build()))
                .withOptions(OllamaOptions.create().withTemperature(0.9))
                .build();

        var response = ollamaApi.chat(request);

        return response.toString();
    }

    public String describeImage(String base64Image) {

        var request = ChatRequest.builder(modelName)
                .withStream(false) // not streaming
                .withMessages(List.of(
                        Message.builder(Role.SYSTEM)
                                .withContent("You are a security monitor. You are looking for anything unusual.")
                                .build(),
                        Message.builder(Role.USER)
                                //.withContent("Compare these two images closely. What, if anything, is different between these two images?")
                                .withContent("What is in this image?")
                                .withImages(List.of(base64Image))
                                .build()))
                .withOptions(OllamaOptions.create().withTemperature(0.9))
                .build();

        var response = ollamaApi.chat(request);

        return response.toString();
    }
}