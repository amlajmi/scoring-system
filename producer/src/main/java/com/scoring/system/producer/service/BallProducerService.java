package com.scoring.system.producer.service;

import java.time.Instant;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.scoring.system.common.BallWonEvent;

@Service
public class BallProducerService {

    private final KafkaTemplate<String, BallWonEvent> kafkaTemplate;

    public BallProducerService(KafkaTemplate<String, BallWonEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBallWonEvent(String playerId) {
        BallWonEvent event = new BallWonEvent(Instant.now(), playerId);
        kafkaTemplate.send("ball-won-events", playerId, event);
    }
}
