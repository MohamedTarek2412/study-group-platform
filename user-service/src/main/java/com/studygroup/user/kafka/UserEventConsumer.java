package com.studygroup.user.kafka;

import com.studygroup.user.kafka.event.UserRegisteredEvent;
import com.studygroup.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for inbound events from the auth-service.
 *
 * <p>Listens on the {@code user.registered} topic and delegates immediately
 * to {@link UserService#handleUserRegistered} — keeping consumer logic thin.
 *
 * <p>Uses Spring Kafka's @RetryableTopic for automatic non-blocking retry
 * with exponential back-off and a Dead Letter Topic on exhaustion.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserService userService;

    /**
     * Handles {@link UserRegisteredEvent} emitted by auth-service.
     *
     * <p>Retry strategy:
     * <ul>
     *   <li>3 attempts with 2 s → 4 s → 8 s exponential back-off</li>
     *   <li>On exhaustion, routed to {@code user.registered-dlt}</li>
     * </ul>
     */
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics    = "${kafka.topics.user-registered}",
            groupId   = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onUserRegistered(ConsumerRecord<String, UserRegisteredEvent> record) {
        UserRegisteredEvent event = record.value();

        log.info("Received UserRegisteredEvent eventId={} authUserId={} username={}",
                event.getEventId(), event.getAuthUserId(), event.getUsername());

        userService.handleUserRegistered(event);
    }
}
