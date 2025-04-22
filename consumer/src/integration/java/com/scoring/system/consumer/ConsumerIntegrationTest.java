package com.scoring.system.consumer;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.scoring.system.common.BallWonEvent;
import com.scoring.system.consumer.service.GameStateMachine;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConsumerIntegrationTest {

    private static final String TOPIC = "ball-won-events";
    
    static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    ).withReuse(true);

    static {
        kafka.start();
    }
    
    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    private KafkaTemplate<String, BallWonEvent> kafkaTemplate;
    
    @Autowired
    private GameStateMachine game;
    
    @BeforeAll
    void setupKafka() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, BallWonEvent> pf = new DefaultKafkaProducerFactory<>(props);
        kafkaTemplate = new KafkaTemplate<>(pf);
    }

    @AfterAll
    void teardown() {
        kafka.stop();
    }
    
    @Test
    void shouldComputeCorrectScoreSequence() throws InterruptedException {
        game.reset();

        String sequence = "ABABAA";
        for (char c : sequence.toCharArray()) {
            kafkaTemplate.send(TOPIC, new BallWonEvent(Instant.now(), String.valueOf(c)));
            Thread.sleep(1000); // wait for consumer to process
        }
        
        assertEquals("Player A wins the game", game.getStatus());
    }

}
