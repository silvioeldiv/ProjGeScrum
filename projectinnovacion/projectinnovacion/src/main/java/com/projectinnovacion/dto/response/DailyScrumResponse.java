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
public class DailyScrumResponse {
    private Long id;
    private Long sprintId;
    private String sprintName;
    private LocalDateTime meetingDate;
    private String notes;
    private List<DailyUpdateResponse> updates;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}