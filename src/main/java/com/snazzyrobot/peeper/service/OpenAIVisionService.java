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
public class OpenAIVisionService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIVisionService.class);

    private final ChatClient chatClient;
    private final String modelName;
    private final OpenAiChatModel chatModel;

    public OpenAIVisionService(OpenAiChatModel chatModel, @Value("${OPENAI_MODEL:gpt-4o}") String modelName) {
        this.chatModel = chatModel;
        this.modelName = modelName;
        this.chatClient = ChatClient.create(chatModel);
    }

    public ChatResponse compareImages(String before, String after) throws IOException, IllegalArgumentException {

        File temp1 = null, temp2 = null;
        ChatResponse response;

        try {
            var img1 = ImageUtil.decodeBase64ToImage(before);
            temp1 = createTempImageFile(img1);
            var imageResource1 = new PathResource(temp1.getAbsolutePath());

            var img2 = ImageUtil.decodeBase64ToImage(after);
            temp2 = createTempImageFile(img2);
            var imageResource2 = new PathResource(temp2.getAbsolutePath());

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
            var userMessage = new UserMessage("Compare these two images, \"before\" and \"after\", closely. What, if anything, is different between these two images? Just describe the image.", new Media(MimeTypeUtils.IMAGE_PNG, imageResource1), new Media(MimeTypeUtils.IMAGE_PNG, imageResource2));

            var options = chatModel.getDefaultOptions();
            response = chatModel.call(new Prompt(List.of(systemMessage, userMessage), OpenAiChatOptions.builder().withModel(modelName).build()));
            logger.info(response.toString());

        } finally {
            if (temp1 != null) {
                temp1.delete();
            }
            if (temp2 != null) {
                temp2.delete();
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