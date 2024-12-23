package com.snazzyrobot.peeper.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ConfigurationProperties.class)
@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "peeper")
public class ConfigurationProperties {
    private String comparisonServiceProvider;
}
