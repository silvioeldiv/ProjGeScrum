package com.projectinnovacion.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprintResponse {
    private Long id;
    private String name;
    private String goal;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
    private Long projectId;
    private String projectName;
    private List<UserStoryResponse> userStories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // Métricas del sprint
    private Integer totalStories;
    private Integer completedStories;
    private Integer totalPoints;
    private Integer completedPoints;
}