package com.snazzyrobot.peeper.configuration;

import com.snazzyrobot.peeper.service.OllamaVisionService;
import com.snazzyrobot.peeper.service.OpenAIVisionService;
import com.snazzyrobot.peeper.service.VisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class ServiceConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ServiceConfiguration.class);

    private static final String DEFAULT_COMPARISON_PROVIDER = "openai";

    private final OpenAiChatModel openAiChatModel;
    private final OllamaChatModel ollamaChatModel;

    // Constructor to inject dependencies manually
    public ServiceConfiguration(OpenAiChatModel openAiChatModel,
                                OllamaChatModel ollamaChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.ollamaChatModel = ollamaChatModel;
    }

    @Bean
    public VisionService visionService(@Value("${peeper.comparison-service-provider:" + DEFAULT_COMPARISON_PROVIDER + "}") String comparisonProvider) {
        return createVisionService(comparisonProvider);
    }

    private VisionService createVisionService(String provider) {
        logger.info("Creating Vision Service with provider: {}", provider);

        if (Objects.equals(provider, "openai")) {
            return new OpenAIVisionService(openAiChatModel, "gpt-4o-mini");
        } else if (Objects.equals(provider, "ollama")) {
            return new OllamaVisionService(ollamaChatModel, "llama3.2:latest");
        }

        throw new IllegalArgumentException("Invalid vision provider: " + provider);
    }
}