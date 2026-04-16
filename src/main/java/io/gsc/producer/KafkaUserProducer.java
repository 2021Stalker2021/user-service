package io.gsc.producer;

import io.gsc.model.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaUserProducer {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private static final String TOPIC = "user-events";

    public void sendMessage(UserEvent event) {
        log.info("Sending event to Kafka");
        kafkaTemplate.send(TOPIC, event);
    }
}
