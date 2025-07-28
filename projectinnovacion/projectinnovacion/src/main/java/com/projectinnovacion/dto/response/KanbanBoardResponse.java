package com.projectinnovacion.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanBoardResponse {
    private Long sprintId;
    private String sprintName;
    private Long projectId;
    private String projectName;
    private Map<String, List<UserStoryResponse>> columns;
    private SprintMetrics metrics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SprintMetrics {
        private Integer totalStories;
        private Integer completedStories;
        private Integer inProgressStories;
        private Integer todoStories;
        private Integer totalPoints;
        private Integer completedPoints;
        private Double completionPercentage;
        private Integer daysRemaining;
    }
}