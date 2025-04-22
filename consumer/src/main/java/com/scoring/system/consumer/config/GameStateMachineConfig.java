package com.scoring.system.consumer.config;


import com.scoring.system.consumer.service.GameStateMachine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(GameStateMachineProperties.class)
public class GameStateMachineConfig {

    @Bean
    public GameStateMachine gameStateMachine(GameStateMachineProperties props) {
        return new GameStateMachine(props.autoReset());
    }
}
