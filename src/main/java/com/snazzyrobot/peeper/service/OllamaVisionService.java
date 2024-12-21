package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.utility.ImageUtil;
import lib.ASCII;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.ChatRequest;
import org.springframework.ai.ollama.api.OllamaApi.Message;
import org.springframework.ai.ollama.api.OllamaApi.Message.Role;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class OllamaVisionService implements VisionService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaVisionService.class);

    private final String modelName;
    private final OllamaApi ollamaApi;
    private final OllamaChatModel chatModel;

    public OllamaVisionService(OllamaApi ollamaApi, OllamaChatModel chatModel, @Value("${OLLAMA_MODEL:llama3.2:latest}") String modelName) {
        this.ollamaApi = ollamaApi;
        this.chatModel = chatModel;
        this.modelName = modelName;
    }

    @Override
    public ChatResponse compareImages(String before, String after) {

        displayImagesInConsole(before, after);

        var request = ChatRequest.builder(modelName).withStream(false) // not streaming
                .withMessages(List.of(Message.builder(Role.SYSTEM).withContent("""
                        You are a security expert. You are looking at two images, "before" and "after".
                        
                        You are looking for the following list of things:
                        1. people entering or exiting the image.
                        2. Items appearing or disappearing from the image.
                        3. A person changing what they are holding, picking up or putting down objects.
                        4. A person changing the activity they are performing.
                        
                        When comparing images, do not worry about contrast or image orientation.
                        Be concise in your descriptions.
                        Each difference you notice between the "before" and "after" images should be formatted on its own line, and begin each line with three asterisks, '***'
                        """).build(), Message.builder(Role.USER)
                        .withContent("""
                                Compare these two images, "before" and "after", closely. What, if anything, is different between these two images?
                                """)
                        .withImages(List.of(before, after)).build()))
                .build();

        var response = ollamaApi.chat(request);

        // TODO Make this work!!!
        return ChatResponse.builder().build();
    }

    private void displayImagesInConsole(String before, String after) {
        ASCII ascii = new ASCII(false, 2, 3);
        try {
            var img1 = ImageUtil.decodeBase64ToImage(before);
            var img2 = ImageUtil.decodeBase64ToImage(after);
            logger.info(ascii.convert(img1));
            logger.info(ascii.convert(img2));
        } catch (IOException e) {
            logger.info("Could not display images as ASCII");
        }
    }

    public String compareImagesUsingCombining(String before, String after) throws IOException {

        var img1 = ImageUtil.decodeBase64ToImage(before);
        var img2 = ImageUtil.decodeBase64ToImage(after);
        var combined = ImageUtil.combineImagesSideBySide(img1, img2);

        var base64Combined = ImageUtil.encodeImageToBase64(combined);

        var request = ChatRequest.builder(modelName).withStream(false) // not streaming
                .withMessages(List.of(Message.builder(Role.SYSTEM).withContent("""
                                You are a security expert. You are looking for anything unusual. \
                                When comparing images, do not worry about contrast or image orientation.\
                                Be concise in your descriptions.
                                Format your response with one difference per line, and begin each line with three amersands, '&&&'""").build(),
                        Message.builder(Role.USER).withContent("The image contains two separate images, on the left and on the right. Compare these two images closely. What, if anything, is different between these two images?")
                                .withImages(List.of(base64Combined)).build()))
                .build();

        var response = ollamaApi.chat(request);

        return response.toString();
    }

    public String describeImage(String base64Image) {

        var request = ChatRequest.builder(modelName).withStream(false) // not streaming
                .withMessages(List.of(Message.builder(Role.SYSTEM).withContent("You are a security monitor. You are looking for anything unusual.").build(), Message.builder(Role.USER)
                        .withContent("What is in this image?").withImages(List.of(base64Image)).build())).withOptions(OllamaOptions.create().withTemperature(0.9)).build();

        var response = ollamaApi.chat(request);

        return response.toString();
    }
}