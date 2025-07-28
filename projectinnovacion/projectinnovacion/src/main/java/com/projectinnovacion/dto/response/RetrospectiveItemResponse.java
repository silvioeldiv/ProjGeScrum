package com.projectinnovacion.dto.response;
import com.projectinnovacion.model.enums.RetrospectiveItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrospectiveItemResponse {
    private Long id;
    private RetrospectiveItemType type;
    private String description;
    private UserDTO createdBy;
    private LocalDateTime createdAt;
}