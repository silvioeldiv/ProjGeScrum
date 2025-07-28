package com.projectinnovacion.dto.response;
import com.projectinnovacion.model.enums.StoryPriority;
import com.projectinnovacion.model.enums.StoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStoryResponse {
    private Long id;
    private String title;
    private String description;
    private String acceptanceCriteria;
    private Integer storyPoints;
    private StoryPriority priority;
    private StoryStatus status;
    private Long projectId;
    private String projectName;
    private UserDTO assignee;
    private UserDTO reporter;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}