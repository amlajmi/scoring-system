package com.scoring.system.consumer.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.scoring.system.consumer.fsm.GameStateMachine;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(GameStateMachineProperties.class)
public class GameStateMachineConfig {

    @Bean
    public GameStateMachine gameStateMachine(GameStateMachineProperties props) {
        return new GameStateMachine(props.autoReset());
    }
}
