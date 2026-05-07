package com.studygroup.discussion.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studygroup.discussion.kafka.event.MembershipEvent;
import com.studygroup.discussion.model.GroupMemberCache;
import com.studygroup.discussion.repository.GroupMemberCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipConsumer {

    private final GroupMemberCacheRepository groupMemberCacheRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "group.member-joined", groupId = "discussion-service-group")
    @Transactional
    public void handleMemberJoined(String message) {
        try {
            MembershipEvent event = objectMapper.readValue(message, MembershipEvent.class);
            log.info("Received MEMBER_JOINED event: groupId={}, userId={}", event.getGroupId(), event.getUserId());

            boolean exists = groupMemberCacheRepository.existsByGroupIdAndUserId(event.getGroupId(), event.getUserId());
            if (!exists) {
                GroupMemberCache cache = GroupMemberCache.builder()
                        .groupId(event.getGroupId())
                        .userId(event.getUserId())
                        .build();
                groupMemberCacheRepository.save(cache);
                log.info("Cached membership: groupId={}, userId={}", event.getGroupId(), event.getUserId());
            }
        } catch (Exception e) {
            log.error("Error processing MEMBER_JOINED event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "group.member-left", groupId = "discussion-service-group")
    @Transactional
    public void handleMemberLeft(String message) {
        try {
            MembershipEvent event = objectMapper.readValue(message, MembershipEvent.class);
            log.info("Received MEMBER_LEFT event: groupId={}, userId={}", event.getGroupId(), event.getUserId());

            groupMemberCacheRepository.findByGroupIdAndUserId(event.getGroupId(), event.getUserId())
                    .ifPresent(cache -> {
                        groupMemberCacheRepository.delete(cache);
                        log.info("Removed membership cache: groupId={}, userId={}", event.getGroupId(), event.getUserId());
                    });
        } catch (Exception e) {
            log.error("Error processing MEMBER_LEFT event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "group.approved", groupId = "discussion-service-group")
    public void handleGroupApproved(String message) {
        try {
            MembershipEvent event = objectMapper.readValue(message, MembershipEvent.class);
            log.info("Group approved: groupId={}", event.getGroupId());
        } catch (Exception e) {
            log.error("Error processing GROUP_APPROVED event: {}", e.getMessage(), e);
        }
    }
}
