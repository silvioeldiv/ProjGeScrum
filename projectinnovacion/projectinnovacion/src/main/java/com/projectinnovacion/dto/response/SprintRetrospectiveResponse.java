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
public class SprintRetrospectiveResponse {
    private Long id;
    private Long sprintId;
    private String sprintName;
    private LocalDateTime retrospectiveDate;
    private String summary;
    private List<RetrospectiveItemResponse> items;
    private List<ActionItemResponse> actionItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}