package com.projectinnovacion.service;
import com.projectinnovacion.dto.request.UpdateStoryStatusRequest;
import com.projectinnovacion.dto.response.KanbanBoardResponse;
import com.projectinnovacion.dto.response.UserStoryResponse;
import com.projectinnovacion.model.Sprint;
import com.projectinnovacion.model.User;
import com.projectinnovacion.model.UserStory;
import com.projectinnovacion.model.enums.StoryStatus;
import com.projectinnovacion.repository.SprintRepository;
import com.projectinnovacion.repository.UserRepository;
import com.projectinnovacion.repository.UserStoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KanbanBoardService {

    private final SprintRepository sprintRepository;
    private final UserStoryRepository userStoryRepository;
    private final UserRepository userRepository;
    private final UserStoryService userStoryService;

    @Transactional(readOnly = true)
    public KanbanBoardResponse getKanbanBoard(Long sprintId) {
        log.debug("Obteniendo tablero Kanban para sprint: {}", sprintId);

        Sprint sprint = sprintRepository.findByIdWithStories(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if (!sprint.getActive()) {
            throw new RuntimeException("El sprint no está activo");
        }

        // Agrupar historias por estado
        Map<String, List<UserStoryResponse>> columns = new LinkedHashMap<>();
        columns.put("TODO", new ArrayList<>());
        columns.put("IN_PROGRESS", new ArrayList<>());
        columns.put("IN_REVIEW", new ArrayList<>());
        columns.put("DONE", new ArrayList<>());

        // Obtener todas las historias del sprint
        List<UserStory> stories = userStoryRepository.findByProjectIdAndStatusIn(
                sprint.getProject().getId(),
                Arrays.asList(StoryStatus.TODO, StoryStatus.IN_PROGRESS,
                        StoryStatus.IN_REVIEW, StoryStatus.DONE)
        );

        // Filtrar solo las historias que pertenecen a este sprint
        stories = stories.stream()
                .filter(story -> story.getSprint() != null && story.getSprint().getId().equals(sprintId))
                .collect(Collectors.toList());

        // Agrupar por estado
        for (UserStory story : stories) {
            UserStoryResponse storyResponse = userStoryService.convertToResponse(story);
            columns.get(story.getStatus().name()).add(storyResponse);
        }

        // Calcular métricas
        KanbanBoardResponse.SprintMetrics metrics = calculateMetrics(sprint, stories);

        return KanbanBoardResponse.builder()
                .sprintId(sprint.getId())
                .sprintName(sprint.getName())
                .projectId(sprint.getProject().getId())
                .projectName(sprint.getProject().getName())
                .columns(columns)
                .metrics(metrics)
                .build();
    }

    @Transactional
    public UserStoryResponse updateStoryStatus(Long storyId, UpdateStoryStatusRequest request) {
        log.info("Actualizando estado de historia {} a {}", storyId, request.getStatus());

        UserStory story = userStoryRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        // Verificar que la historia esté en un sprint activo
        if (story.getSprint() == null || !story.getSprint().getActive()) {
            throw new RuntimeException("La historia no está en un sprint activo");
        }

        // Actualizar estado
        story.setStatus(request.getStatus());

        // Actualizar assignee si se proporciona
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            story.setAssignee(assignee);
        }

        // Si se marca como completada, actualizar fecha
        if (request.getStatus() == StoryStatus.DONE && story.getCompletedAt() == null) {
            story.setCompletedAt(LocalDateTime.now());
        }

        UserStory updatedStory = userStoryRepository.save(story);
        log.info("Estado de historia actualizado exitosamente");

        return userStoryService.convertToResponse(updatedStory);
    }

    @Transactional
    public UserStoryResponse moveStoryToColumn(Long storyId, StoryStatus newStatus) {
        log.info("Moviendo historia {} a columna {}", storyId, newStatus);

        UpdateStoryStatusRequest request = UpdateStoryStatusRequest.builder()
                .status(newStatus)
                .build();

        return updateStoryStatus(storyId, request);
    }

    @Transactional(readOnly = true)
    public KanbanBoardResponse getActiveSprintBoard(Long projectId) {
        log.debug("Obteniendo tablero Kanban del sprint activo para proyecto: {}", projectId);

        Sprint activeSprint = sprintRepository.findActiveSprintByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("No hay sprint activo para este proyecto"));

        return getKanbanBoard(activeSprint.getId());
    }

    private KanbanBoardResponse.SprintMetrics calculateMetrics(Sprint sprint, List<UserStory> stories) {
        int totalStories = stories.size();
        int completedStories = (int) stories.stream()
                .filter(story -> story.getStatus() == StoryStatus.DONE)
                .count();
        int inProgressStories = (int) stories.stream()
                .filter(story -> story.getStatus() == StoryStatus.IN_PROGRESS)
                .count();
        int todoStories = (int) stories.stream()
                .filter(story -> story.getStatus() == StoryStatus.TODO)
                .count();

        int totalPoints = stories.stream()
                .mapToInt(story -> story.getStoryPoints() != null ? story.getStoryPoints() : 0)
                .sum();

        int completedPoints = stories.stream()
                .filter(story -> story.getStatus() == StoryStatus.DONE)
                .mapToInt(story -> story.getStoryPoints() != null ? story.getStoryPoints() : 0)
                .sum();

        double completionPercentage = totalStories > 0 ?
                (double) completedStories / totalStories * 100 : 0.0;

        int daysRemaining = (int) ChronoUnit.DAYS.between(
                LocalDateTime.now(),
                sprint.getEndDate()
        );

        return KanbanBoardResponse.SprintMetrics.builder()
                .totalStories(totalStories)
                .completedStories(completedStories)
                .inProgressStories(inProgressStories)
                .todoStories(todoStories)
                .totalPoints(totalPoints)
                .completedPoints(completedPoints)
                .completionPercentage(completionPercentage)
                .daysRemaining(Math.max(0, daysRemaining))
                .build();
    }
}