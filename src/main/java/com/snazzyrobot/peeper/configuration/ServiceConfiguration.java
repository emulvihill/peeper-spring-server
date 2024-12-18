package com.snazzyrobot.peeper.configuration;

import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import com.snazzyrobot.peeper.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class ServiceConfiguration {

    private static final String DEFAULT_COMPARISON_PROVIDER = "openai";

    private final ComparisonProcessorService comparisonProcessorService;
    private final VideoSnapRepository videoSnapRepository;
    private final OpenAIVisionService openAIVisionService;
    private final OllamaVisionService ollamaVisionService;

    // Constructor to inject dependencies manually
    public ServiceConfiguration(ComparisonProcessorService comparisonProcessorService, VideoSnapRepository videoSnapRepository, OpenAIVisionService openAIVisionService, OllamaVisionService ollamaVisionService) {
        this.comparisonProcessorService = comparisonProcessorService;
        this.videoSnapRepository = videoSnapRepository;
        this.openAIVisionService = openAIVisionService;
        this.ollamaVisionService = ollamaVisionService;
    }

    @Bean
    public ComparisonService comparisonService(@Value("${peeper.comparison-service-provider:" + DEFAULT_COMPARISON_PROVIDER + "}") String comparisonProvider) {
        return createComparisonService(comparisonProvider);
    }

    private ComparisonService createComparisonService(String provider) {
        if (Objects.equals(provider, "openai")) {
            return new OpenAIComparisonService(comparisonProcessorService, videoSnapRepository, openAIVisionService);
        } else if (Objects.equals(provider, "ollama")) {
            return new OllamaComparisonService(comparisonProcessorService, videoSnapRepository, ollamaVisionService);
        }

        throw new IllegalArgumentException("Invalid comparison provider: " + provider);
    }
}