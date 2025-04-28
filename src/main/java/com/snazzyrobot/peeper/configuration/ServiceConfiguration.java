package com.snazzyrobot.peeper.configuration;

import com.snazzyrobot.peeper.service.*;
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
    private static final String DEFAULT_COMPARISON_MODEL = "gpt-4o-mini";

    private final SettingService settingsService;
    private final OpenAiChatModel openAiChatModel;
    private final OllamaChatModel ollamaChatModel;
    private final UserMessageBuilder userMessageBuilder;

    // Constructor to inject dependencies manually
    public ServiceConfiguration(SettingService settingsService,
                                OpenAiChatModel openAiChatModel,
                                OllamaChatModel ollamaChatModel,
                                UserMessageBuilder userMessageBuilder) {
        this.settingsService = settingsService;
        this.openAiChatModel = openAiChatModel;
        this.ollamaChatModel = ollamaChatModel;
        this.userMessageBuilder = userMessageBuilder;
    }

    @Bean
    public VisionService visionService(@Value("${peeper.comparison-service-provider:" + DEFAULT_COMPARISON_PROVIDER + "}") String comparisonProvider) {

        String provider = settingsService.getSetting("comparison_provider").orElse(DEFAULT_COMPARISON_PROVIDER);
        String model = settingsService.getSetting("comparison_model").orElse(DEFAULT_COMPARISON_MODEL);

        return createVisionService(provider, model);
    }

    private VisionService createVisionService(String provider, String model) {
        logger.info("Creating Vision Service with provider: {} and model: {}", provider, model);

        if (Objects.equals(provider, "openai")) {
            return new OpenAIVisionService(openAiChatModel, userMessageBuilder, model);
        } else if (Objects.equals(provider, "ollama")) {
            return new OllamaVisionService(ollamaChatModel, userMessageBuilder, model);
        }

        throw new IllegalArgumentException("Invalid vision provider: " + provider + " & model " + model);
    }
}