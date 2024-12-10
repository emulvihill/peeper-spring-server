package com.snazzyrobot.peeper.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsFilterConfiguration {

    final Environment env;

    public CorsFilterConfiguration(Environment env) {
        this.env = env;
    }

    @Value("${spring.graphql.cors.allowed-origins}")
    private String allowedOriginsEnv;

    @Bean
    public CorsFilter corsFilter() {

        // String allowedOriginsEnv = env.getProperty("ALLOWED_ORIGINS");
        List<String> allowedOrigins;

        if (allowedOriginsEnv == null || allowedOriginsEnv.isEmpty()) {
            allowedOrigins = List.of();
        } else {
            allowedOrigins = Arrays.asList(allowedOriginsEnv.split(","));
        }
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(allowedOrigins);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/graphql/**", config);
        return new CorsFilter(source);
    }
}
