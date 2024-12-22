package com.snazzyrobot.peeper.configuration;

import com.snazzyrobot.peeper.repository.SnapComparisonRepository;
import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import com.snazzyrobot.peeper.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class ServiceConfiguration {

    private static final String DEFAULT_COMPARISON_PROVIDER = "openai";

    // Constructor to inject dependencies manually
    public ServiceConfiguration(ComparisonProcessorService comparisonProcessorService,
                                VideoSnapRepository videoSnapRepository,
                                SnapComparisonRepository comparisonRepository,
                                OpenAIVisionService openAIVisionService,
                                OllamaVisionService ollamaVisionService) {

    }

    @Bean
    public VisionService visionService(@Value("${peeper.comparison-service-provider:" + DEFAULT_COMPARISON_PROVIDER + "}") String comparisonProvider) {
        return createVisionService(comparisonProvider);
    }

    private VisionService createVisionService(String provider) {
        if (Objects.equals(provider, "openai")) {
            return new OpenAIVisionService();
        } else if (Objects.equals(provider, "ollama")) {
            return new OllamaVisionService();
        }

        throw new IllegalArgumentException("Invalid vision provider: " + provider);
    }
}