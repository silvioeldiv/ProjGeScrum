package com.projectinnovacion.service;


import com.projectinnovacion.dto.request.ProjectRequest;
import com.projectinnovacion.dto.response.ProjectResponse;
import com.projectinnovacion.dto.response.UserDTO;
import com.projectinnovacion.model.Project;
import com.projectinnovacion.model.User;
import com.projectinnovacion.repository.ProjectRepository;
import com.projectinnovacion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        log.info("Creando nuevo proyecto: {}", request.getName());

        if (projectRepository.existsByName(request.getName())) {
            throw new RuntimeException("Ya existe un proyecto con ese nombre");
        }

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(true)
                .sprintDuration(request.getSprintDuration() != null ? request.getSprintDuration() : 2)
                .build();

        // Asignar Product Owner
        if (request.getProductOwnerId() != null) {
            User productOwner = userRepository.findById(request.getProductOwnerId())
                    .orElseThrow(() -> new RuntimeException("Product Owner no encontrado"));
            project.setProductOwner(productOwner);
        }

        // Asignar Scrum Master
        if (request.getScrumMasterId() != null) {
            User scrumMaster = userRepository.findById(request.getScrumMasterId())
                    .orElseThrow(() -> new RuntimeException("Scrum Master no encontrado"));
            project.setScrumMaster(scrumMaster);
        }

        // Asignar miembros
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            Set<User> members = new HashSet<>();
            for (Long memberId : request.getMemberIds()) {
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Miembro no encontrado: " + memberId));
                members.add(member);
            }
            project.setMembers(members);
        }

        Project savedProject = projectRepository.save(project);
        log.info("Proyecto creado con ID: {}", savedProject.getId());
        return convertToResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        log.debug("Obteniendo todos los proyectos activos");
        return projectRepository.findAllActive().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        log.debug("Obteniendo proyecto con ID: {}", id);
        Project project = projectRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        return convertToResponse(project);
    }
    public ProjectResponse addMemberToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        project.getMembers().add(user);
        Project savedProject = projectRepository.save(project);

        return convertToResponse(savedProject);
    }
    public void removeMemberFromProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        project.getMembers().remove(user);
        projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByUser(Long userId) {
        log.debug("Obteniendo proyectos del usuario: {}", userId);
        return projectRepository.findByUserInvolved(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        log.info("Actualizando proyecto con ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        if (request.getProductOwnerId() != null) {
            User productOwner = userRepository.findById(request.getProductOwnerId())
                    .orElseThrow(() -> new RuntimeException("Product Owner no encontrado"));
            project.setProductOwner(productOwner);
        }

        if (request.getScrumMasterId() != null) {
            User scrumMaster = userRepository.findById(request.getScrumMasterId())
                    .orElseThrow(() -> new RuntimeException("Scrum Master no encontrado"));
            project.setScrumMaster(scrumMaster);
        }

        if (request.getMemberIds() != null) {
            Set<User> members = new HashSet<>();
            for (Long memberId : request.getMemberIds()) {
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("Miembro no encontrado: " + memberId));
                members.add(member);
            }
            project.setMembers(members);
        }

        Project updatedProject = projectRepository.save(project);
        log.info("Proyecto actualizado con ID: {}", updatedProject.getId());
        return convertToResponse(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        log.info("Eliminando proyecto con ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        project.setActive(false);
        projectRepository.save(project);
        log.info("Proyecto marcado como inactivo: {}", id);
    }

    private ProjectResponse convertToResponse(Project project) {
        ProjectResponse.ProjectResponseBuilder responseBuilder = ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .active(project.getActive())
                .createdAt(project.getCreatedAt())
                .sprintDuration(project.getSprintDuration()) // Agregar esta línea
                .updatedAt(project.getUpdatedAt());

        if (project.getProductOwner() != null) {
            responseBuilder.productOwner(convertUserToDTO(project.getProductOwner()));
        }

        if (project.getScrumMaster() != null) {
            responseBuilder.scrumMaster(convertUserToDTO(project.getScrumMaster()));
        }

        if (project.getMembers() != null) {
            Set<UserDTO> memberDTOs = project.getMembers().stream()
                    .map(this::convertUserToDTO)
                    .collect(Collectors.toSet());
            responseBuilder.members(memberDTOs);
        }

        return responseBuilder.build();
    }

    private UserDTO convertUserToDTO(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.getActive())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }

}
