package com.studygroup.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Admin payload to approve or reject a creator application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatorApprovalRequest {

    @NotNull(message = "Decision is required (APPROVE or REJECT)")
    private Decision decision;

    @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
    private String reason; // mandatory when decision = REJECT

    public enum Decision {
        APPROVE,
        REJECT
    }
}