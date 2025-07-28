package com.projectinnovacion.dto.response;
import com.projectinnovacion.model.enums.ActionItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionItemResponse {
    private Long id;
    private String description;
    private UserDTO assignedTo;
    private ActionItemStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}