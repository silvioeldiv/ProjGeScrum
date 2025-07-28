package com.projectinnovacion.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprintRetrospectiveRequest {

    @NotNull(message = "El ID del sprint es requerido")
    private Long sprintId;

    private LocalDateTime retrospectiveDate;

    private String summary;
}