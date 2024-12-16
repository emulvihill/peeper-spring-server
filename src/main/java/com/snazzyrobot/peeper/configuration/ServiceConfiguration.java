package com.snazzyrobot.peeper.configuration;

import com.snazzyrobot.peeper.repository.VideoSnapRepository;
import com.snazzyrobot.peeper.service.OpenAIService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Assuming OpenAIComparisonService implements ComparisonService or is assignable to it
import com.snazzyrobot.peeper.service.OpenAIComparisonService;
import com.snazzyrobot.peeper.service.ComparisonService;

@Configuration
public class ServiceConfiguration {

    private final VideoSnapRepository videoSnapRepository;
    private final OpenAIService openAIService;

    // Constructor to inject dependencies manually
    public ServiceConfiguration(VideoSnapRepository videoSnapRepository, OpenAIService openAIService) {
        this.videoSnapRepository = videoSnapRepository;
        this.openAIService = openAIService;
    }

    @Bean
    public ComparisonService comparisonService() {
        return new OpenAIComparisonService(videoSnapRepository, openAIService);
    }
}