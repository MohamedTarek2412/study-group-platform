package com.studygroup.group.dto;

import com.studygroup.group.model.GroupStatus;
import com.studygroup.group.model.MeetingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDto {
    private Long id;
    private String name;
    private String subject;
    private String description;
    private Long creatorId;
    private String creatorName;
    private Integer maxMembers;
    private Integer currentMembers;
    private MeetingType meetingType;
    private String meetingSchedule;
    private String location;
    private GroupStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
