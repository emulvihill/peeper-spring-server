package com.snazzyrobot.peeper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snazzyrobot.peeper.utility.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OllamaVisionService extends VisionService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaVisionService.class);

    private final String modelName;
    private final OllamaChatModel chatModel;

    public OllamaVisionService(OllamaChatModel chatModel, @Value("${OLLAMA_MODEL:llama3.2:latest}") String modelName) {
        this.chatModel = chatModel;
        this.modelName = modelName;
    }

    @Override
    public List<String> compareImages(String before, String after) throws IOException {

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
                    OllamaOptions.builder()
                            .model(modelName)
                            .format(new ObjectMapper().readValue(jsonSchema, Map.class))
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

/*    public String compareImagesUsingCombining(String before, String after) throws IOException {

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
    }*/

}