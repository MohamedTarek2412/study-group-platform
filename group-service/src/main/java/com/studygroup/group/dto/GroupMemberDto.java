package com.studygroup.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberDto {
    private Long id;
    private Long groupId;
    private Long userId;
    private String userName;
    private LocalDateTime joinedAt;
}
