package com.projectinnovacion.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class SprintRequest {

    @NotBlank(message = "El nombre del sprint es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @Size(max = 500, message = "El objetivo no puede exceder 500 caracteres")
    private String goal;

    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de fin es requerida")
    private LocalDateTime endDate;

    @NotNull(message = "El ID del proyecto es requerido")
    private Long projectId;

    private Set<Long> userStoryIds;
}
