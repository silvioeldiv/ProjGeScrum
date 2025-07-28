package com.projectinnovacion.controller;
import com.projectinnovacion.dto.request.*;
import com.projectinnovacion.dto.response.*;
import com.projectinnovacion.service.SprintReviewRetrospectiveService;
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
@RequestMapping("/api/sprint-review-retrospective")
public class SprintReviewRetrospectiveController {

    private final SprintReviewRetrospectiveService service;

    // Sprint Review endpoints

    @PostMapping("/review")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('PRODUCT_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<SprintReviewResponse> createSprintReview(@Valid @RequestBody SprintReviewRequest request) {
        log.info("Creando Sprint Review");
        SprintReviewResponse response = service.createSprintReview(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/review/{id}")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('PRODUCT_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<SprintReviewResponse> updateSprintReview(
            @PathVariable Long id,
            @Valid @RequestBody SprintReviewRequest request) {
        log.info("Actualizando Sprint Review con ID: {}", id);
        SprintReviewResponse response = service.updateSprintReview(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/review/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintReviewResponse> getSprintReviewById(@PathVariable Long id) {
        log.info("Obteniendo Sprint Review con ID: {}", id);
        SprintReviewResponse response = service.getSprintReviewById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/review/sprint/{sprintId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintReviewResponse> getSprintReviewBySprintId(@PathVariable Long sprintId) {
        log.info("Obteniendo Sprint Review del sprint: {}", sprintId);
        SprintReviewResponse response = service.getSprintReviewBySprintId(sprintId);
        return ResponseEntity.ok(response);
    }

    // Sprint Retrospective endpoints

    @PostMapping("/retrospective")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<SprintRetrospectiveResponse> createSprintRetrospective(@Valid @RequestBody SprintRetrospectiveRequest request) {
        log.info("Creando Sprint Retrospective");
        SprintRetrospectiveResponse response = service.createSprintRetrospective(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/retrospective/{retrospectiveId}/item")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RetrospectiveItemResponse> addRetrospectiveItem(
            @PathVariable Long retrospectiveId,
            @Valid @RequestBody RetrospectiveItemRequest request) {
        log.info("Agregando ítem a retrospectiva: {}", retrospectiveId);
        RetrospectiveItemResponse response = service.addRetrospectiveItem(retrospectiveId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/retrospective/{retrospectiveId}/action")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<ActionItemResponse> addActionItem(
            @PathVariable Long retrospectiveId,
            @Valid @RequestBody ActionItemRequest request) {
        log.info("Agregando acción a retrospectiva: {}", retrospectiveId);
        ActionItemResponse response = service.addActionItem(retrospectiveId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/action/{actionItemId}")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<ActionItemResponse> updateActionItem(
            @PathVariable Long actionItemId,
            @Valid @RequestBody ActionItemRequest request) {
        log.info("Actualizando acción con ID: {}", actionItemId);
        ActionItemResponse response = service.updateActionItem(actionItemId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrospective/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintRetrospectiveResponse> getRetrospectiveById(@PathVariable Long id) {
        log.info("Obteniendo retrospectiva con ID: {}", id);
        SprintRetrospectiveResponse response = service.getRetrospectiveById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/retrospective/sprint/{sprintId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SprintRetrospectiveResponse> getRetrospectiveBySprintId(@PathVariable Long sprintId) {
        log.info("Obteniendo retrospectiva del sprint: {}", sprintId);
        SprintRetrospectiveResponse response = service.getRetrospectiveBySprintId(sprintId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/action/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<ActionItemResponse>> getActionItemsByUser(@PathVariable Long userId) {
        log.info("Obteniendo acciones del usuario: {}", userId);
        List<ActionItemResponse> response = service.getActionItemsByUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/action/pending")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<List<ActionItemResponse>> getPendingActionItems() {
        log.info("Obteniendo acciones pendientes");
        List<ActionItemResponse> response = service.getPendingActionItems();
        return ResponseEntity.ok(response);
    }
}