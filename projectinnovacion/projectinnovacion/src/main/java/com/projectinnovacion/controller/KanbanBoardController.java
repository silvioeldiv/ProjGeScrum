package com.projectinnovacion.controller;


import com.projectinnovacion.dto.request.UpdateStoryStatusRequest;
import com.projectinnovacion.dto.response.KanbanBoardResponse;
import com.projectinnovacion.dto.response.UserStoryResponse;
import com.projectinnovacion.model.enums.StoryStatus;
import com.projectinnovacion.service.KanbanBoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/kanban")
public class KanbanBoardController {

    private final KanbanBoardService kanbanBoardService;

    @GetMapping("/sprint/{sprintId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KanbanBoardResponse> getKanbanBoard(@PathVariable Long sprintId) {
        log.info("Obteniendo tablero Kanban para sprint: {}", sprintId);
        KanbanBoardResponse board = kanbanBoardService.getKanbanBoard(sprintId);
        return ResponseEntity.ok(board);
    }

    @GetMapping("/project/{projectId}/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KanbanBoardResponse> getActiveSprintBoard(@PathVariable Long projectId) {
        log.info("Obteniendo tablero Kanban del sprint activo para proyecto: {}", projectId);
        KanbanBoardResponse board = kanbanBoardService.getActiveSprintBoard(projectId);
        return ResponseEntity.ok(board);
    }

    @PutMapping("/story/{storyId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserStoryResponse> updateStoryStatus(
            @PathVariable Long storyId,
            @Valid @RequestBody UpdateStoryStatusRequest request) {
        log.info("Actualizando estado de historia: {}", storyId);
        UserStoryResponse response = kanbanBoardService.updateStoryStatus(storyId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/story/{storyId}/move")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserStoryResponse> moveStoryToColumn(
            @PathVariable Long storyId,
            @RequestParam StoryStatus status) {
        log.info("Moviendo historia {} a columna {}", storyId, status);
        UserStoryResponse response = kanbanBoardService.moveStoryToColumn(storyId, status);
        return ResponseEntity.ok(response);
    }
}
