package com.snazzyrobot.peeper.service;

import com.snazzyrobot.peeper.utility.Base64Samples;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import java.io.IOException;
import java.util.List;

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
        Generation mockGeneration = mock(Generation.class);
        when(mockGeneration.getOutput()).thenReturn(new AssistantMessage("{\"comparisons\": [], \"numPersons\": 1}"));
        when(mockResponse.getResult()).thenReturn(mockGeneration);
        when(mockResponse.toString()).thenReturn("Comparison result");
        when(mockChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        String modelName = "gpt-4.0";
        OpenAIVisionService service = new OpenAIVisionService(mockChatModel, modelName);

        // Execute method under test
        var resultEntry = service.compareImages(Base64Samples.base64Star, Base64Samples.base64Star);
        var rawResult = resultEntry.getKey();
        var result = resultEntry.getValue();
        // Assertions
        assertEquals("Mock", rawResult.substring(0, 4));
        assertEquals(1, result.getNumPersons());
        assertEquals(List.of(), result.getComparisons());

        // Verify interactions
        verify(mockChatModel, times(1)).call(any(Prompt.class));
    }

    @Test
    void compareImages_shouldThrowIllegalArgumentException_whenInvalidImageFormatIsProvided() {

        // Mock dependencies
        OpenAiChatModel mockChatModel = mock(OpenAiChatModel.class);
        String modelName = "gpt-4.0";
        OpenAIVisionService service = new OpenAIVisionService(mockChatModel, modelName);

        // Assertions
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            service.compareImages("(*&^%$#@)", Base64Samples.base64Star);
        });

        assertTrue(exception.getMessage().contains("Illegal base64 character"));
    }
}