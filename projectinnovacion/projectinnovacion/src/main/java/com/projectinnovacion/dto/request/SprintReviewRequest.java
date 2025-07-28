package com.projectinnovacion.dto.request;


import jakarta.validation.constraints.NotNull;
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
public class SprintReviewRequest {

    @NotNull(message = "El ID del sprint es requerido")
    private Long sprintId;

    private LocalDateTime reviewDate;

    private String summary;

    private String demoNotes;

    private String feedback;

    private String clientComments;

    private Set<String> attendees;

    private Set<String> deliverableUrls;
}