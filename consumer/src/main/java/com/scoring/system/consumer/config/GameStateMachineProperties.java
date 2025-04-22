package com.scoring.system.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game.state-machine")
public record GameStateMachineProperties(boolean autoReset) {
}
