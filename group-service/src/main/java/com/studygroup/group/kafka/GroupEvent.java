package com.studygroup.group.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupEvent {
    private String eventType; // GROUP_CREATED, GROUP_APPROVED, GROUP_REJECTED, MEMBER_JOINED, MEMBER_LEFT
    private Long groupId;
    private Long userId;
    private String userName;
    private String groupName;
    private LocalDateTime timestamp;
}
