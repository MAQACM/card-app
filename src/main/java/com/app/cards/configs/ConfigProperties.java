package com.app.cards.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix="cards.ms")
public class ConfigProperties {
    private long tokenExpiry;
    private String signKey;
}
