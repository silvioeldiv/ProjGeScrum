package com.projectinnovacion.dto.request;


import lombok.Data;
import java.util.Set;

@Data
public class RoleAssignmentDTO {
    private Set<String> roles;
}