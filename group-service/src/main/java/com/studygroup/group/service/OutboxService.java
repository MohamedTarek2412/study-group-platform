package com.studygroup.group.service;

import com.studygroup.group.model.OutboxEvent;
import com.studygroup.group.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public void enqueue(String topic, String eventType, String payload) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .topic(topic)
                .eventType(eventType)
                .payload(payload)
                .build();
        outboxEventRepository.save(outboxEvent);
    }
}
