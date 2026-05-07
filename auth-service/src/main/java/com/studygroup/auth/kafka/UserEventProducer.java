package com.studygroup.auth.kafka;

import com.studygroup.auth.kafka.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Kafka producer for publishing user-related events.
 *
 * <p>Publishes {@link UserRegisteredEvent} to {@code auth.user-registered} topic.
 * Errors are logged but don't block the auth flow (graceful degradation).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-registered:auth.user-registered}")
    private String userRegisteredTopic;

    /**
     * Publishes a UserRegisteredEvent to the Kafka topic.
     *
     * <p>Asynchronously sends the event and logs the result without blocking registration flow.
     */
    public void publishUserRegistered(UserRegisteredEvent event) {
        try {
            String messageKey = event.getAuthUserId().toString();

            Message<UserRegisteredEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, userRegisteredTopic)
                    .setHeader(KafkaHeaders.KEY, messageKey)
                    .setHeader("eventId", event.getEventId().toString())
                    .setHeader("source", event.getSource())
                    .build();

            kafkaTemplate.send(message).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Published UserRegisteredEvent: eventId={}, authUserId={}, email={}",
                            event.getEventId(), event.getAuthUserId(), event.getEmail());
                } else {
                    log.warn("Failed to publish UserRegisteredEvent: eventId={}, reason={}",
                            event.getEventId(), ex.getMessage());
                }
            });

        } catch (Exception ex) {
            // Keep auth flow resilient; registration succeeds even if Kafka is unavailable
            log.warn("Exception while publishing UserRegisteredEvent: {}", ex.getMessage(), ex);
        }
    }

    public void publishUserRegistered(String email, String role) {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .authUserId(UUID.randomUUID())
                .email(email)
                .role(role)
                .build();
        publishUserRegistered(event);
    }
}

