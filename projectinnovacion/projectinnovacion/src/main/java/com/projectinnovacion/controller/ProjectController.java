package com.projectinnovacion.controller;
import com.projectinnovacion.dto.request.ProjectRequest;
import com.projectinnovacion.dto.response.ProjectResponse;
import com.projectinnovacion.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCRUM_MASTER') or hasRole('PRODUCT_OWNER')")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        log.info("Solicitud para crear proyecto: {}", request.getName());
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.ok(response);
    }
    //aqui
    @PostMapping("/{projectId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCRUM_MASTER') or hasRole('PRODUCT_OWNER')")
    public ResponseEntity<ProjectResponse> addMemberToProject(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        ProjectResponse response = projectService.addMemberToProject(projectId, userId);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        log.info("Obteniendo lista de todos los proyectos");
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
    @DeleteMapping("/{projectId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCRUM_MASTER') or hasRole('PRODUCT_OWNER')")
    public ResponseEntity<Void> removeMemberFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        projectService.removeMemberFromProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        log.info("Obteniendo proyecto con ID: {}", id);
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<ProjectResponse>> getProjectsByUser(@PathVariable Long userId) {
        log.info("Obteniendo proyectos del usuario: {}", userId);
        List<ProjectResponse> projects = projectService.getProjectsByUser(userId);
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCRUM_MASTER') or hasRole('PRODUCT_OWNER')")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        log.info("Actualizando proyecto con ID: {}", id);
        ProjectResponse response = projectService.updateProject(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.info("Eliminando proyecto con ID: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}