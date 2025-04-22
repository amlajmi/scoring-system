package com.scoring.system.consumer.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.scoring.system.common.BallWonEvent;
import com.scoring.system.consumer.fsm.GameStateMachine;
import com.scoring.system.consumer.fsm.GameStatus;

@Service
public class BallConsumerService {

    private final GameStateMachine fsm;

    public BallConsumerService(GameStateMachine fsm) {
        this.fsm = fsm;
    }
    
    @KafkaListener(topics = "ball-won-events")
    public void onBallWon(BallWonEvent event) {
        System.out.println("Received event: " + event);
        fsm.pointWonBy(event.playerId());
        System.out.println(fsm.getStatusMessage());

        if (fsm.getStatus() == GameStatus.PLAYER_A_WINS || fsm.getStatus() == GameStatus.PLAYER_B_WINS) {
            if (fsm.shouldAutoReset()) {
                fsm.reset();
                System.out.println("New game!");
            }
        }
    }

}
