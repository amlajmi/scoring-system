package com.scoring.system.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.scoring.system.common.BallWonEvent;
import com.scoring.system.common.KafkaTestContainerExtension;
import com.scoring.system.consumer.fsm.GameStateMachine;
import com.scoring.system.consumer.fsm.GameStatus;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConsumerIntegrationTest {

    @RegisterExtension
    static final KafkaTestContainerExtension kafkaExtension = new KafkaTestContainerExtension();

    private KafkaTemplate<String, BallWonEvent> kafkaTemplate;

    @Autowired
    private GameStateMachine game;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KafkaTestContainerExtension::getBootstrapServers);
    }

    @BeforeAll
    void setupKafkaTemplate() {
        kafkaTemplate = kafkaExtension.createKafkaTemplate();
    }

    @Test
    void shouldComputeCorrectScoreSequence() throws InterruptedException {
        game.reset();
        String sequence = "ABABAA";
        
        for (char c : sequence.toCharArray()) {
            kafkaTemplate.send(KafkaTestContainerExtension.TOPIC, new BallWonEvent(Instant.now(), String.valueOf(c)));
            Thread.sleep(1000); // allow time for the consumer to process
        }

        assertEquals(GameStatus.PLAYER_A_WINS, game.getStatus());
    }
}
