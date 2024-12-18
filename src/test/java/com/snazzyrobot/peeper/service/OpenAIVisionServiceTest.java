package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.utility.Base64Samples;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenAIVisionServiceTest {

    @BeforeAll
    static void beforeAll() {
        //Mockito.mockStatic(ImageIO.class);
    }

    @Test
    void compareImages_shouldReturnChatResponse_whenBothImagesAreValid() throws IOException {

        // Mock dependencies
        OpenAiChatModel mockChatModel = mock(OpenAiChatModel.class);
        ChatResponse mockResponse = mock(ChatResponse.class);
        when(mockResponse.toString()).thenReturn("Comparison result");
        when(mockChatModel.call(any(Prompt.class))).thenReturn(mockResponse);
        ComparisonProcessorService mockCps = mock(ComparisonProcessorService.class);

        String modelName = "gpt-4.0";
        OpenAIVisionService service = new OpenAIVisionService(mockCps, mockChatModel, modelName);

        // Execute method under test
        ChatResponse result = service.compareImages(Base64Samples.base64Star, Base64Samples.base64Star);

        // Assertions
        assertEquals("Comparison result", result.toString());

        // Verify interactions
        verify(mockChatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    void compareImages_shouldThrowIllegalArgumentException_whenInvalidImageFormatIsProvided() {

        // Mock dependencies
        OpenAiChatModel mockChatModel = mock(OpenAiChatModel.class);
        ComparisonProcessorService mockCps = mock(ComparisonProcessorService.class);
        String modelName = "gpt-4.0";
        OpenAIVisionService service = new OpenAIVisionService(mockCps, mockChatModel, modelName);

        // Assertions
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.compareImages("(*&^%$#@)", Base64Samples.base64Star);
        });

        assertTrue(exception.getMessage().contains("Illegal base64 character"));
    }
}