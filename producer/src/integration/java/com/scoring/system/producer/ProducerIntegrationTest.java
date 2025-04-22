package com.scoring.system.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.scoring.system.common.BallWonEvent;
import com.scoring.system.common.KafkaTestContainerExtension;

@Testcontainers
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProducerIntegrationTest {

    @RegisterExtension
    static final KafkaTestContainerExtension kafkaExtension = new KafkaTestContainerExtension();

    private KafkaTemplate<String, BallWonEvent> kafkaTemplate;

    @BeforeAll
    void setupKafka() {
        kafkaTemplate = kafkaExtension.createKafkaTemplate();
    }

    @Test
    void shouldPublishCorrectEventsToKafka() {
        String sequence = "ABABAA";

        // Send messages
        for (char c : sequence.toCharArray()) {
            kafkaTemplate.send(KafkaTestContainerExtension.TOPIC, new BallWonEvent(Instant.now(), String.valueOf(c)));
        }

        // Consume and verify
        try (Consumer<String, BallWonEvent> consumer = kafkaExtension.createConsumer("test-consumer")) {
            consumer.subscribe(List.of(KafkaTestContainerExtension.TOPIC));

            List<BallWonEvent> received = new ArrayList<>();
            long deadline = System.currentTimeMillis() + 5000;

            while (received.size() < sequence.length() && System.currentTimeMillis() < deadline) {
                ConsumerRecords<String, BallWonEvent> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, BallWonEvent> record : records) {
                    received.add(record.value());
                }
            }

            assertEquals(sequence.length(), received.size(), "Should have received the correct number of events");

            for (int i = 0; i < sequence.length(); i++) {
                assertEquals(
                    String.valueOf(sequence.charAt(i)),
                    received.get(i).playerId(),
                    "Mismatch at index " + i
                );
            }
        }
    }
}
