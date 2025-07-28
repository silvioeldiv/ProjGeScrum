package com.projectinnovacion.controller;


import com.projectinnovacion.dto.request.UserStoryRequest;
import com.projectinnovacion.dto.response.UserStoryResponse;
import com.projectinnovacion.model.enums.StoryStatus;
import com.projectinnovacion.service.UserStoryService;
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
@RequestMapping("/api/stories")
public class UserStoryController {

    private final UserStoryService userStoryService;

    @PostMapping
    @PreAuthorize("hasRole('PRODUCT_OWNER') or hasRole('SCRUM_MASTER') or hasRole('ADMIN') or hasRole('DEVELOPER')")
    public ResponseEntity<UserStoryResponse> createUserStory(@Valid @RequestBody UserStoryRequest request) {
        log.info("Creando nueva historia de usuario: {}", request.getTitle());
        UserStoryResponse response = userStoryService.createUserStory(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserStoryResponse>> getStoriesByProject(@PathVariable Long projectId) {
        log.info("Obteniendo historias del proyecto: {}", projectId);
        List<UserStoryResponse> stories = userStoryService.getStoriesByProject(projectId);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/project/{projectId}/backlog")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserStoryResponse>> getBacklogByProject(@PathVariable Long projectId) {
        log.info("Obteniendo backlog del proyecto: {}", projectId);
        List<UserStoryResponse> backlog = userStoryService.getBacklogByProject(projectId);
        return ResponseEntity.ok(backlog);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserStoryResponse> getStoryById(@PathVariable Long id) {
        log.info("Obteniendo historia con ID: {}", id);
        UserStoryResponse story = userStoryService.getStoryById(id);
        return ResponseEntity.ok(story);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRODUCT_OWNER') or hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<UserStoryResponse> updateUserStory(
            @PathVariable Long id,
            @Valid @RequestBody UserStoryRequest request) {
        log.info("Actualizando historia con ID: {}", id);
        UserStoryResponse response = userStoryService.updateUserStory(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserStoryResponse> updateStoryStatus(
            @PathVariable Long id,
            @RequestParam StoryStatus status) {
        log.info("Actualizando estado de historia {} a {}", id, status);
        UserStoryResponse response = userStoryService.updateStoryStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRODUCT_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserStory(@PathVariable Long id) {
        log.info("Eliminando historia con ID: {}", id);
        userStoryService.deleteUserStory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assignee/{assigneeId}")
    @PreAuthorize("hasRole('ADMIN') or #assigneeId == authentication.principal.id")
    public ResponseEntity<List<UserStoryResponse>> getStoriesByAssignee(@PathVariable Long assigneeId) {
        log.info("Obteniendo historias asignadas al usuario: {}", assigneeId);
        List<UserStoryResponse> stories = userStoryService.getStoriesByAssignee(assigneeId);
        return ResponseEntity.ok(stories);
    }
}