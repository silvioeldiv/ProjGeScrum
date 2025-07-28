package com.projectinnovacion.dto.request;
import com.projectinnovacion.model.enums.ActionItemStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionItemRequest {

    @NotBlank(message = "La descripción es requerida")
    private String description;

    private Long assignedToId;

    private ActionItemStatus status;

    private LocalDateTime dueDate;
}