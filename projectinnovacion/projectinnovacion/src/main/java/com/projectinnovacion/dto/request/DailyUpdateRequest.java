package com.projectinnovacion.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyUpdateRequest {

    @NotBlank(message = "El trabajo de ayer es requerido")
    private String yesterdayWork;

    @NotBlank(message = "El plan de hoy es requerido")
    private String todayPlan;

    private String blockers;
}