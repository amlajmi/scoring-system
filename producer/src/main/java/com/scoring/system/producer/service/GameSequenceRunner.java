package com.scoring.system.producer.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GameSequenceRunner implements ApplicationRunner {

    private final BallProducerService producer;

    public GameSequenceRunner(BallProducerService producer) {
        this.producer = producer;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("sequence")) {
            System.err.println("Missing --sequence= argument. Exiting.");
            return;
        }

        String sequence = args.getOptionValues("sequence").get(0);
        for (char c : sequence.toCharArray()) {
            if (c == 'A' || c == 'B') {
                producer.sendBallWonEvent(String.valueOf(c));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
