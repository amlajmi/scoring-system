package com.scoring.system.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.scoring.system.common.BallWonEvent;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProducerIntegrationTest {

    private static final String TOPIC = "ball-won-events";

    static final ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    ).withReuse(true);

    static {
        kafka.start();
    }
    
    private KafkaTemplate<String, BallWonEvent> kafkaTemplate;

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
    void shouldPublishCorrectEventsToKafka() {
        String sequence = "ABABAA";

        // Send messages
        for (char c : sequence.toCharArray()) {
            kafkaTemplate.send(TOPIC, new BallWonEvent(Instant.now(), String.valueOf(c)));
        }

        // Consume and verify
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<String, BallWonEvent> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(List.of(TOPIC));

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

        consumer.close();
    }
}
