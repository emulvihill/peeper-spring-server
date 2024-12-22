package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.utility.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
public class OpenAIVisionService implements VisionService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIVisionService.class);

    private final ChatClient chatClient;
    private final String modelName;
    private final OpenAiChatModel chatModel;

    public OpenAIVisionService(OpenAiChatModel chatModel, @Value("${OPENAI_MODEL:gpt-4o-mini}") String modelName) {
        this.chatModel = chatModel;
        this.modelName = modelName;
        this.chatClient = ChatClient.create(chatModel);
    }

    public ChatResponse compareImages(String before, String after) throws IllegalArgumentException, IOException {

        File beforeTmp = null, afterTmp = null;
        ChatResponse response;

        try {
            var beforeImg = ImageUtil.decodeBase64ToImage(before);
            beforeTmp = createTempImageFile(beforeImg);
            var beforeResource = new PathResource(beforeTmp.getAbsolutePath());

            var afterImg = ImageUtil.decodeBase64ToImage(after);
            afterTmp = createTempImageFile(afterImg);
            var afterResource = new PathResource(afterTmp.getAbsolutePath());

            var systemMessage = new SystemMessage("""
                        You are a security expert. You are looking at two images, "before" and "after".
                    
                        You are looking for the following list of things:
                        1. people entering or exiting the image.
                        2. Items appearing or disappearing from the image.
                        3. A person changing what they are holding, picking up or putting down objects.
                        4. A person changing the activity they are performing.
                        5. None of the above conditions are detected. In this case state "No differences detected".
                    
                        When comparing images, do not worry about contrast or image orientation.
                        Be concise in your descriptions.
                        Each difference you notice between the "before" and "after" images should be formatted on its own line, and begin each line with three asterisks, '***'.
                    """);

            var userMessage = new UserMessage("Compare these two images, \"before\" and \"after\", closely. What, if anything, is different between these two images? Just describe the image.",
                    new Media(MimeTypeUtils.IMAGE_PNG, beforeResource), new Media(MimeTypeUtils.IMAGE_PNG, afterResource));

            var options = chatModel.getDefaultOptions();
            response = chatModel.call(new Prompt(List.of(systemMessage, userMessage), OpenAiChatOptions.builder().withModel(modelName).build()));
            logger.info(response.toString());

        } finally {
            if (beforeTmp != null) {
                beforeTmp.delete();
            }
            if (afterTmp != null) {
                afterTmp.delete();
            }
        }

        return response;
    }

    private File createTempImageFile(BufferedImage image) throws IOException {
        File tempFile = Files.createTempFile(null, ".png").toFile();
        ImageIO.write(image, "png", tempFile);
        return tempFile;
    }

}