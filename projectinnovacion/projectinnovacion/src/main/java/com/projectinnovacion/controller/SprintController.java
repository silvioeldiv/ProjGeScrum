package com.projectinnovacion.controller;
import com.projectinnovacion.dto.request.SprintRequest;
import com.projectinnovacion.dto.response.SprintResponse;
import com.projectinnovacion.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/sprints")
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<SprintResponse> createSprint(@Valid @RequestBody SprintRequest request) {
        log.info("Creando nuevo sprint: {}", request.getName());
        SprintResponse response = sprintService.createSprint(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SprintResponse>> getSprintsByProject(@PathVariable Long projectId) {
        log.info("Obteniendo sprints del proyecto: {}", projectId);
        List<SprintResponse> sprints = sprintService.getSprintsByProject(projectId);
        return ResponseEntity.ok(sprints);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintResponse> getSprintById(@PathVariable Long id) {
        log.info("Obteniendo sprint con ID: {}", id);
        SprintResponse sprint = sprintService.getSprintById(id);
        return ResponseEntity.ok(sprint);
    }

    @GetMapping("/project/{projectId}/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintResponse> getActiveSprint(@PathVariable Long projectId) {
        log.info("Obteniendo sprint activo del proyecto: {}", projectId);
        SprintResponse sprint = sprintService.getActiveSprint(projectId);
        return ResponseEntity.ok(sprint);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<SprintResponse> updateSprint(
            @PathVariable Long id,
            @Valid @RequestBody SprintRequest request) {
        log.info("Actualizando sprint con ID: {}", id);
        SprintResponse response = sprintService.updateSprint(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<SprintResponse> startSprint(@PathVariable Long id) {
        log.info("Iniciando sprint con ID: {}", id);
        SprintResponse response = sprintService.startSprint(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<SprintResponse> completeSprint(@PathVariable Long id) {
        log.info("Completando sprint con ID: {}", id);
        SprintResponse response = sprintService.completeSprint(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/stories")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('PRODUCT_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<SprintResponse> assignStoriesToSprint(
            @PathVariable Long id,
            @RequestBody Set<Long> storyIds) {
        log.info("Asignando historias al sprint: {}", id);
        SprintResponse response = sprintService.assignStoriesToSprint(id, storyIds);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/stories")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('PRODUCT_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<SprintResponse> removeStoriesFromSprint(
            @PathVariable Long id,
            @RequestBody Set<Long> storyIds) {
        log.info("Removiendo historias del sprint: {}", id);
        SprintResponse response = sprintService.removeStoriesFromSprint(id, storyIds);
        return ResponseEntity.ok(response);
    }
}
