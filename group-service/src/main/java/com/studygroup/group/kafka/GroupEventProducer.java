package com.studygroup.group.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studygroup.group.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupEventProducer {

    private final ObjectMapper objectMapper;
    private final OutboxService outboxService;

    public void publishGroupCreatedEvent(Long groupId, Long creatorId, String creatorName, String groupName) {
        GroupEvent event = GroupEvent.builder()
                .eventType("GROUP_CREATED")
                .groupId(groupId)
                .userId(creatorId)
                .userName(creatorName)
                .groupName(groupName)
                .timestamp(LocalDateTime.now())
                .build();
        publishEvent("group.created", event);
    }

    public void publishGroupApprovedEvent(Long groupId, String groupName) {
        GroupEvent event = GroupEvent.builder()
                .eventType("GROUP_APPROVED")
                .groupId(groupId)
                .groupName(groupName)
                .timestamp(LocalDateTime.now())
                .build();
        publishEvent("group.approved", event);
    }

    public void publishGroupRejectedEvent(Long groupId, String groupName) {
        GroupEvent event = GroupEvent.builder()
                .eventType("GROUP_REJECTED")
                .groupId(groupId)
                .groupName(groupName)
                .timestamp(LocalDateTime.now())
                .build();
        publishEvent("group.rejected", event);
    }

    public void publishMemberJoinedEvent(Long groupId, Long userId, String userName, String groupName) {
        GroupEvent event = GroupEvent.builder()
                .eventType("MEMBER_JOINED")
                .groupId(groupId)
                .userId(userId)
                .userName(userName)
                .groupName(groupName)
                .timestamp(LocalDateTime.now())
                .build();
        publishEvent("group.member-joined", event);
    }

    private void publishEvent(String topic, GroupEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            outboxService.enqueue(topic, event.getEventType(), eventJson);
            log.info("Event enqueued to outbox for topic {}: {}", topic, event.getEventType());
        } catch (Exception e) {
            log.error("Error enqueueing event for topic {}: {}", topic, e.getMessage());
        }
    }
}
