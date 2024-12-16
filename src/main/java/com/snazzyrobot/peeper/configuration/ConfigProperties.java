package com.snazzyrobot.peeper.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "peeper")
public class ConfigProperties {
    private String comparisonServiceProvider;
}
