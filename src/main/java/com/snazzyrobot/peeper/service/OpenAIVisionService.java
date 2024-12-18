package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.utility.ImageUtil;
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

    private final ChatClient chatClient;
    private final String modelName;
    private final OpenAiChatModel chatModel;

    public OpenAIVisionService(OpenAiChatModel chatModel, @Value("${OPENAI_MODEL:gpt-4o}") String modelName) {
        this.chatModel = chatModel;
        this.modelName = modelName;
        this.chatClient = ChatClient.create(chatModel);
    }

    public String compareImages(String before, String after) throws IOException, IllegalArgumentException {

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
                    You are a security expert. You are looking for anything unusual. \
                    When comparing images, do not worry about contrast or image orientation.\
                    Be concise in your descriptions.
                    Format your response with one difference per line, and begin each line with three asterisks, '***'""");
            var userMessage = new UserMessage("Explain what differences do you see between these two pictures? Just describe the image.", new Media(MimeTypeUtils.IMAGE_PNG, imageResource1), new Media(MimeTypeUtils.IMAGE_PNG, imageResource2));

            var options = chatModel.getDefaultOptions();
            response = chatModel.call(new Prompt(List.of(systemMessage, userMessage), OpenAiChatOptions.builder().withModel(modelName).build()));

        } finally {
            if (temp1 != null) {
                temp1.delete();
            }
            if (temp2 != null) {
                temp2.delete();
            }
        }

        return response.toString();
    }

    private File createTempImageFile(BufferedImage image) throws IOException {
        File tempFile = Files.createTempFile(null, ".png").toFile();
        ImageIO.write(image, "png", tempFile);
        return tempFile;
    }

}