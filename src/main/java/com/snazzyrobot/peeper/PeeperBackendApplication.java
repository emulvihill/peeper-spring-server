package com.snazzyrobot.peeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(dateTimeProviderRef = "offsetDateTimeProvider")
public class PeeperBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeeperBackendApplication.class, args);
    }
}
