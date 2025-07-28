package com.projectinnovacion.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class RoleAssignmentRequest {
    private Set<String> roles;
}

