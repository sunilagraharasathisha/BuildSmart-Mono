package com.buildsmart.siteops.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Used by a Project Manager to review and comment on a SiteLog.
 */
public record SiteLogReviewRequest(

        @NotBlank(message = "reviewedBy (user ID) is required")
        String reviewedBy,

        @Size(max = 1000, message = "Reviewer comments must not exceed 1000 characters")
        String reviewerComments
) {}
