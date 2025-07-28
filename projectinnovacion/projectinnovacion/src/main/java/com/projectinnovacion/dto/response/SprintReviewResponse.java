package com.projectinnovacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprintReviewResponse {
    private Long id;
    private Long sprintId;
    private String sprintName;
    private LocalDateTime reviewDate;
    private String summary;
    private String demoNotes;
    private String feedback;
    private String clientComments;
    private Set<String> attendees;
    private Set<String> deliverableUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}