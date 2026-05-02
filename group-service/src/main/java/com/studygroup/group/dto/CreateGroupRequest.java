package com.studygroup.group.dto;

import com.studygroup.group.model.MeetingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupRequest {
    @NotBlank(message = "Group name is required")
    private String name;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Maximum members is required")
    @Positive(message = "Maximum members must be greater than 0")
    private Integer maxMembers;

    @NotNull(message = "Meeting type is required")
    private MeetingType meetingType;

    @NotBlank(message = "Meeting schedule is required")
    private String meetingSchedule;

    private String location; // Optional, required only for offline meetings
}
