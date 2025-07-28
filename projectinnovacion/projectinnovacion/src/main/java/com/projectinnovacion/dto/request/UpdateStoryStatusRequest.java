package com.projectinnovacion.dto.request;



import com.projectinnovacion.model.enums.StoryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoryStatusRequest {

    @NotNull(message = "El estado es requerido")
    private StoryStatus status;

    private Long assigneeId;
    private String comment;
}