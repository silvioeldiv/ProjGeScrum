package com.projectinnovacion.service;


import com.projectinnovacion.dto.request.UserStoryRequest;
import com.projectinnovacion.dto.response.UserStoryResponse;
import com.projectinnovacion.dto.response.UserDTO;

import com.projectinnovacion.model.Project;
import com.projectinnovacion.model.User;
import com.projectinnovacion.model.UserStory;
import com.projectinnovacion.model.enums.StoryStatus;
import com.projectinnovacion.repository.ProjectRepository;
import com.projectinnovacion.repository.UserRepository;
import com.projectinnovacion.repository.UserStoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserStoryResponse createUserStory(UserStoryRequest request) {
        log.info("Creando nueva historia de usuario: {}", request.getTitle());

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        User reporter = getCurrentUser();

        UserStory userStory = UserStory.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .acceptanceCriteria(request.getAcceptanceCriteria())
                .storyPoints(request.getStoryPoints())
                .priority(request.getPriority())
                .status(request.getStatus() != null ? request.getStatus() : StoryStatus.BACKLOG)
                .project(project)
                .reporter(reporter)
                .build();

        // Asignar assignee si se proporciona
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Usuario asignado no encontrado"));
            userStory.setAssignee(assignee);
        }

        // Establecer orden automáticamente si no se proporciona
        if (request.getOrderIndex() == null) {
            Integer maxOrder = userStoryRepository.findMaxOrderIndexByProjectId(request.getProjectId());
            userStory.setOrderIndex(maxOrder != null ? maxOrder + 1 : 1);
        } else {
            userStory.setOrderIndex(request.getOrderIndex());
        }

        UserStory savedStory = userStoryRepository.save(userStory);
        log.info("Historia de usuario creada con ID: {}", savedStory.getId());

        return convertToResponse(savedStory);
    }

    @Transactional(readOnly = true)
    public List<UserStoryResponse> getStoriesByProject(Long projectId) {
        log.debug("Obteniendo historias del proyecto: {}", projectId);

        return userStoryRepository.findByProjectIdOrderByOrderIndexAndPriority(projectId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserStoryResponse> getBacklogByProject(Long projectId) {
        log.debug("Obteniendo backlog del proyecto: {}", projectId);

        return userStoryRepository.findBacklogByProjectId(projectId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserStoryResponse getStoryById(Long id) {
        log.debug("Obteniendo historia con ID: {}", id);

        UserStory story = userStoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        return convertToResponse(story);
    }

    @Transactional
    public UserStoryResponse updateUserStory(Long id, UserStoryRequest request) {
        log.info("Actualizando historia con ID: {}", id);

        UserStory story = userStoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        story.setTitle(request.getTitle());
        story.setDescription(request.getDescription());
        story.setAcceptanceCriteria(request.getAcceptanceCriteria());
        story.setStoryPoints(request.getStoryPoints());
        story.setPriority(request.getPriority());

        if (request.getStatus() != null) {
            story.setStatus(request.getStatus());
        }

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Usuario asignado no encontrado"));
            story.setAssignee(assignee);
        } else {
            story.setAssignee(null);
        }

        if (request.getOrderIndex() != null) {
            story.setOrderIndex(request.getOrderIndex());
        }

        UserStory updatedStory = userStoryRepository.save(story);
        log.info("Historia actualizada con ID: {}", updatedStory.getId());

        return convertToResponse(updatedStory);
    }

    @Transactional
    public void deleteUserStory(Long id) {
        log.info("Eliminando historia con ID: {}", id);

        if (!userStoryRepository.existsById(id)) {
            throw new RuntimeException("Historia no encontrada");
        }

        userStoryRepository.deleteById(id);
        log.info("Historia eliminada con ID: {}", id);
    }

    @Transactional
    public UserStoryResponse updateStoryStatus(Long id, StoryStatus newStatus) {
        log.info("Actualizando estado de historia {} a {}", id, newStatus);

        UserStory story = userStoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        story.setStatus(newStatus);
        UserStory updatedStory = userStoryRepository.save(story);

        return convertToResponse(updatedStory);
    }

    @Transactional(readOnly = true)
    public List<UserStoryResponse> getStoriesByAssignee(Long assigneeId) {
        log.debug("Obteniendo historias asignadas al usuario: {}", assigneeId);

        return userStoryRepository.findByAssigneeId(assigneeId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

   public UserStoryResponse convertToResponse(UserStory story) {
        UserStoryResponse.UserStoryResponseBuilder responseBuilder = UserStoryResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .description(story.getDescription())
                .acceptanceCriteria(story.getAcceptanceCriteria())
                .storyPoints(story.getStoryPoints())
                .priority(story.getPriority())
                .status(story.getStatus())
                .projectId(story.getProject().getId())
                .projectName(story.getProject().getName())
                .orderIndex(story.getOrderIndex())
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .completedAt(story.getCompletedAt());

        if (story.getAssignee() != null) {
            responseBuilder.assignee(convertUserToDTO(story.getAssignee()));
        }

        if (story.getReporter() != null) {
            responseBuilder.reporter(convertUserToDTO(story.getReporter()));
        }

        return responseBuilder.build();
    }

    private UserDTO convertUserToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }


}