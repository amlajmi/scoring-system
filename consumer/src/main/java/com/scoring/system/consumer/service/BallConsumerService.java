package com.scoring.system.consumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.scoring.system.common.BallWonEvent;

@Service
public class BallConsumerService {

    private final GameStateMachine fsm = new GameStateMachine();

    @KafkaListener(topics = "ball-won-events")
    public void onBallWon(BallWonEvent event) {
        System.out.println("Received event: " + event);
        fsm.pointWonBy(event.playerId());
        System.out.println(fsm.getStatus());
        
        if (fsm.getStatus().contains("wins the game")) {
            fsm.reset();
            System.out.println("New game!");
        }
    }
}
