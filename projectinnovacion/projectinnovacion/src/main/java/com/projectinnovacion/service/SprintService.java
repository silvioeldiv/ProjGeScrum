package com.projectinnovacion.service;
import com.projectinnovacion.dto.request.SprintRequest;
import com.projectinnovacion.dto.response.SprintResponse;
import com.projectinnovacion.dto.response.UserStoryResponse;
import com.projectinnovacion.model.Project;
import com.projectinnovacion.model.Sprint;
import com.projectinnovacion.model.UserStory;
import com.projectinnovacion.model.enums.StoryStatus;
import com.projectinnovacion.repository.ProjectRepository;
import com.projectinnovacion.repository.SprintRepository;
import com.projectinnovacion.repository.UserStoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final UserStoryRepository userStoryRepository;
    private final UserStoryService userStoryService;

    @Transactional
    public SprintResponse createSprint(SprintRequest request) {
        log.info("Creando nuevo sprint: {}", request.getName());

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Verificar si ya hay un sprint activo
        if (sprintRepository.findActiveSprintByProjectId(request.getProjectId()).isPresent()) {
            throw new RuntimeException("Ya existe un sprint activo para este proyecto");
        }

        Sprint sprint = Sprint.builder()
                .name(request.getName())
                .goal(request.getGoal())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .project(project)
                .active(false) // Se activa manualmente cuando comienza
                .build();

        Sprint savedSprint = sprintRepository.save(sprint);

        // Asignar historias al sprint si se proporcionan
        if (request.getUserStoryIds() != null && !request.getUserStoryIds().isEmpty()) {
            assignStoriesToSprint(savedSprint.getId(), request.getUserStoryIds());
        }

        log.info("Sprint creado con ID: {}", savedSprint.getId());
        return convertToResponse(savedSprint);
    }

    @Transactional(readOnly = true)
    public List<SprintResponse> getSprintsByProject(Long projectId) {
        log.debug("Obteniendo sprints del proyecto: {}", projectId);

        return sprintRepository.findByProjectIdOrderByStartDateDesc(projectId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SprintResponse getSprintById(Long id) {
        log.debug("Obteniendo sprint con ID: {}", id);

        Sprint sprint = sprintRepository.findByIdWithStories(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        return convertToResponse(sprint);
    }

    @Transactional(readOnly = true)
    public SprintResponse getActiveSprint(Long projectId) {
        log.debug("Obteniendo sprint activo del proyecto: {}", projectId);

        Sprint sprint = sprintRepository.findActiveSprintByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("No hay sprint activo para este proyecto"));

        return convertToResponse(sprint);
    }

    @Transactional
    public SprintResponse updateSprint(Long id, SprintRequest request) {
        log.info("Actualizando sprint con ID: {}", id);

        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        sprint.setName(request.getName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());

        Sprint updatedSprint = sprintRepository.save(sprint);
        log.info("Sprint actualizado con ID: {}", updatedSprint.getId());

        return convertToResponse(updatedSprint);
    }

    @Transactional
    public SprintResponse startSprint(Long id) {
        log.info("Iniciando sprint con ID: {}", id);

        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        // Verificar si ya hay un sprint activo
        if (sprintRepository.findActiveSprintByProjectId(sprint.getProject().getId()).isPresent()) {
            throw new RuntimeException("Ya existe un sprint activo para este proyecto");
        }

        sprint.setActive(true);
        sprint.setStartDate(LocalDateTime.now());

        // Cambiar estado de historias a TODO
        for (UserStory story : sprint.getUserStories()) {
            if (story.getStatus() == StoryStatus.BACKLOG) {
                story.setStatus(StoryStatus.TODO);
                userStoryRepository.save(story);
            }
        }

        Sprint activeSprint = sprintRepository.save(sprint);
        log.info("Sprint iniciado con ID: {}", activeSprint.getId());

        return convertToResponse(activeSprint);
    }

    @Transactional
    public SprintResponse completeSprint(Long id) {
        log.info("Completando sprint con ID: {}", id);

        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if (!sprint.getActive()) {
            throw new RuntimeException("El sprint no está activo");
        }

        sprint.setActive(false);
        sprint.setCompletedAt(LocalDateTime.now());

        // Mover historias no completadas de vuelta al backlog
        for (UserStory story : sprint.getUserStories()) {
            if (story.getStatus() != StoryStatus.DONE) {
                story.setStatus(StoryStatus.BACKLOG);
                story.setSprint(null);
                userStoryRepository.save(story);
            }
        }

        Sprint completedSprint = sprintRepository.save(sprint);
        log.info("Sprint completado con ID: {}", completedSprint.getId());

        return convertToResponse(completedSprint);
    }

    @Transactional
    public SprintResponse assignStoriesToSprint(Long sprintId, Set<Long> storyIds) {
        log.info("Asignando {} historias al sprint {}", storyIds.size(), sprintId);

        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if (sprint.getActive()) {
            throw new RuntimeException("No se pueden agregar historias a un sprint activo");
        }

        Set<UserStory> stories = new HashSet<>();
        for (Long storyId : storyIds) {
            UserStory story = userStoryRepository.findById(storyId)
                    .orElseThrow(() -> new RuntimeException("Historia no encontrada: " + storyId));

            if (story.getSprint() != null) {
                throw new RuntimeException("La historia " + storyId + " ya está asignada a otro sprint");
            }

            story.setSprint(sprint);
            stories.add(story);
        }

        sprint.setUserStories(stories);
        Sprint updatedSprint = sprintRepository.save(sprint);

        return convertToResponse(updatedSprint);
    }

    @Transactional
    public SprintResponse removeStoriesFromSprint(Long sprintId, Set<Long> storyIds) {
        log.info("Removiendo {} historias del sprint {}", storyIds.size(), sprintId);

        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if (sprint.getActive()) {
            throw new RuntimeException("No se pueden remover historias de un sprint activo");
        }

        for (Long storyId : storyIds) {
            UserStory story = userStoryRepository.findById(storyId)
                    .orElseThrow(() -> new RuntimeException("Historia no encontrada: " + storyId));

            if (story.getSprint() != null && story.getSprint().getId().equals(sprintId)) {
                story.setSprint(null);
                story.setStatus(StoryStatus.BACKLOG);
                userStoryRepository.save(story);
            }
        }

        Sprint updatedSprint = sprintRepository.findByIdWithStories(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        return convertToResponse(updatedSprint);
    }

    private SprintResponse convertToResponse(Sprint sprint) {
        SprintResponse.SprintResponseBuilder responseBuilder = SprintResponse.builder()
                .id(sprint.getId())
                .name(sprint.getName())
                .goal(sprint.getGoal())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .active(sprint.getActive())
                .projectId(sprint.getProject().getId())
                .projectName(sprint.getProject().getName())
                .createdAt(sprint.getCreatedAt())
                .updatedAt(sprint.getUpdatedAt())
                .completedAt(sprint.getCompletedAt());

        // Convertir historias y calcular métricas
        if (sprint.getUserStories() != null) {
            List<UserStoryResponse> stories = sprint.getUserStories().stream()
                    .map(userStoryService::convertToResponse)
                    .collect(Collectors.toList());

            responseBuilder.userStories(stories);

            // Calcular métricas
            int totalStories = stories.size();
            int completedStories = (int) stories.stream()
                    .filter(story -> story.getStatus() == StoryStatus.DONE)
                    .count();

            int totalPoints = stories.stream()
                    .mapToInt(story -> story.getStoryPoints() != null ? story.getStoryPoints() : 0)
                    .sum();

            int completedPoints = stories.stream()
                    .filter(story -> story.getStatus() == StoryStatus.DONE)
                    .mapToInt(story -> story.getStoryPoints() != null ? story.getStoryPoints() : 0)
                    .sum();

            responseBuilder
                    .totalStories(totalStories)
                    .completedStories(completedStories)
                    .totalPoints(totalPoints)
                    .completedPoints(completedPoints);
        }

        return responseBuilder.build();
    }
}