package com.studygroup.discussion.kafka.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MembershipEvent {
    private String eventType;
    private Long groupId;
    private Long userId;
    private String userName;
    private String groupName;
    private LocalDateTime timestamp;
}
