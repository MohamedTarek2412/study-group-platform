package com.studygroup.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReplyRequest {
    @NotBlank(message = "Content is required")
    private String content;
}
