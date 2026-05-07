package com.studygroup.group.service;

import com.studygroup.group.model.OutboxEvent;
import com.studygroup.group.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms:5000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc();
        for (OutboxEvent outboxEvent : pendingEvents) {
            try {
                kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getPayload());
                outboxEvent.setPublishedAt(LocalDateTime.now());
                outboxEvent.setLastError(null);
            } catch (Exception ex) {
                outboxEvent.setAttempts(outboxEvent.getAttempts() + 1);
                outboxEvent.setLastError(ex.getMessage());
                log.error("Failed publishing outbox event {} to topic {}: {}", outboxEvent.getId(),
                        outboxEvent.getTopic(), ex.getMessage());
            }
        }
    }
}
