package com.projectinnovacion.dto.request;
import com.projectinnovacion.model.enums.StoryPriority;
import com.projectinnovacion.model.enums.StoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStoryRequest {

    @NotBlank(message = "El título es requerido")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String title;

    private String description;

    private String acceptanceCriteria;

    private Integer storyPoints;

    @NotNull(message = "La prioridad es requerida")
    private StoryPriority priority;

    private StoryStatus status;

    @NotNull(message = "El ID del proyecto es requerido")
    private Long projectId;

    private Long assigneeId;

    private Integer orderIndex;
}