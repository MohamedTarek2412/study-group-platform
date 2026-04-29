package com.studygroup.user.kafka.producer;

import com.studygroup.user.kafka.event.CreatorApprovedEvent;
import com.studygroup.user.kafka.event.CreatorRejectedEvent;
import com.studygroup.user.kafka.event.UserProfileUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer for all outbound events emitted by the user-service.
 *
 * <p>Each publish method is non-blocking; failures are logged and can be
 * wired into a Dead Letter Topic (DLT) at the KafkaTemplate level.
 *
 * <p>Single Responsibility: knows ONLY how to send events to Kafka.
 * It does not know anything about business rules.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-profile-updated}")
    private String profileUpdatedTopic;

    @Value("${kafka.topics.creator-approved}")
    private String creatorApprovedTopic;

    @Value("${kafka.topics.creator-rejected}")
    private String creatorRejectedTopic;

    // ─── Publish Methods ──────────────────────────────────────────────────

    public void publishProfileUpdated(UserProfileUpdatedEvent event) {
        send(profileUpdatedTopic, event.getProfileId().toString(), event);
    }

    public void publishCreatorApproved(CreatorApprovedEvent event) {
        send(creatorApprovedTopic, event.getProfileId().toString(), event);
    }

    public void publishCreatorRejected(CreatorRejectedEvent event) {
        send(creatorRejectedTopic, event.getProfileId().toString(), event);
    }

    // ─── Core Send ────────────────────────────────────────────────────────

    private void send(String topic, String key, Object payload) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, payload);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish to topic={} key={} error={}",
                        topic, key, ex.getMessage(), ex);
            } else {
                log.debug("Published to topic={} partition={} offset={}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
