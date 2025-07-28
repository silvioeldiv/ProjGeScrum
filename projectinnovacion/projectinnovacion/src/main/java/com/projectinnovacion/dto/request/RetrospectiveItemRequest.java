package com.projectinnovacion.dto.request;


import com.projectinnovacion.model.enums.RetrospectiveItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrospectiveItemRequest {

    @NotNull(message = "El tipo es requerido")
    private RetrospectiveItemType type;

    @NotBlank(message = "La descripción es requerida")
    private String description;
}