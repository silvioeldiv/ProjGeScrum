package com.projectinnovacion.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyUpdateResponse {
    private Long id;
    private UserDTO user;
    private String yesterdayWork;
    private String todayPlan;
    private String blockers;
    private LocalDateTime createdAt;
}
