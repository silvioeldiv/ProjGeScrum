package com.projectinnovacion.service;

import com.projectinnovacion.dto.request.*;
import com.projectinnovacion.dto.response.*;
import com.projectinnovacion.model.*;
import com.projectinnovacion.model.enums.ActionItemStatus;
import com.projectinnovacion.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SprintReviewRetrospectiveService {

    private final SprintReviewRepository sprintReviewRepository;
    private final SprintRetrospectiveRepository sprintRetrospectiveRepository;
    private final ActionItemRepository actionItemRepository;
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;

    // Sprint Review methods

    @Transactional
    public SprintReviewResponse createSprintReview(SprintReviewRequest request) {
        log.info("Creando Sprint Review para sprint: {}", request.getSprintId());

        Sprint sprint = sprintRepository.findById(request.getSprintId())
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        // Verificar si ya existe una review para este sprint
        sprintReviewRepository.findBySprintId(sprint.getId())
                .ifPresent(review -> {
                    throw new RuntimeException("Ya existe una review para este sprint");
                });

        SprintReview review = SprintReview.builder()
                .sprint(sprint)
                .reviewDate(request.getReviewDate() != null ? request.getReviewDate() : LocalDateTime.now())
                .summary(request.getSummary())
                .demoNotes(request.getDemoNotes())
                .feedback(request.getFeedback())
                .clientComments(request.getClientComments())
                .attendees(request.getAttendees())
                .deliverableUrls(request.getDeliverableUrls())
                .build();

        SprintReview savedReview = sprintReviewRepository.save(review);
        log.info("Sprint Review creada con ID: {}", savedReview.getId());

        return convertReviewToResponse(savedReview);
    }

    @Transactional
    public SprintRetrospectiveResponse createSprintRetrospective(SprintRetrospectiveRequest request) {
        log.info("Creando Sprint Retrospective para sprint: {}", request.getSprintId());

        Sprint sprint = sprintRepository.findById(request.getSprintId())
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        // Verificar si ya existe una retrospectiva para este sprint
        sprintRetrospectiveRepository.findBySprintId(sprint.getId())
                .ifPresent(retro -> {
                    throw new RuntimeException("Ya existe una retrospectiva para este sprint");
                });

        SprintRetrospective retrospective = SprintRetrospective.builder()
                .sprint(sprint)
                .retrospectiveDate(request.getRetrospectiveDate() != null ?
                        request.getRetrospectiveDate() : LocalDateTime.now())
                .summary(request.getSummary())
                .build();

        SprintRetrospective savedRetrospective = sprintRetrospectiveRepository.save(retrospective);
        log.info("Sprint Retrospective creada con ID: {}", savedRetrospective.getId());

        return convertRetrospectiveToResponse(savedRetrospective);
    }

    @Transactional
    public RetrospectiveItemResponse addRetrospectiveItem(Long retrospectiveId, RetrospectiveItemRequest request) {
        log.info("Agregando ítem a retrospectiva: {}", retrospectiveId);

        SprintRetrospective retrospective = sprintRetrospectiveRepository.findById(retrospectiveId)
                .orElseThrow(() -> new RuntimeException("Retrospectiva no encontrada"));

        User currentUser = getCurrentUser();

        RetrospectiveItem item = RetrospectiveItem.builder()
                .retrospective(retrospective)
                .type(request.getType())
                .description(request.getDescription())
                .createdBy(currentUser)
                .build();

        retrospective.getItems().add(item);
        sprintRetrospectiveRepository.save(retrospective);

        log.info("Ítem agregado a retrospectiva");

        return convertItemToResponse(item);
    }

    @Transactional
    public ActionItemResponse addActionItem(Long retrospectiveId, ActionItemRequest request) {
        log.info("Agregando acción a retrospectiva: {}", retrospectiveId);

        SprintRetrospective retrospective = sprintRetrospectiveRepository.findById(retrospectiveId)
                .orElseThrow(() -> new RuntimeException("Retrospectiva no encontrada"));

        ActionItem actionItem = ActionItem.builder()
                .retrospective(retrospective)
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : ActionItemStatus.PENDING)
                .dueDate(request.getDueDate())
                .build();

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            actionItem.setAssignedTo(assignedTo);
        }

        ActionItem savedActionItem = actionItemRepository.save(actionItem);
        log.info("Acción creada con ID: {}", savedActionItem.getId());

        return convertActionItemToResponse(savedActionItem);
    }
    @Transactional(readOnly = true)
    public SprintReviewResponse getSprintReviewBySprintId(Long sprintId) {
        log.debug("Obteniendo Sprint Review del sprint: {}", sprintId);

        SprintReview review = sprintReviewRepository.findBySprintId(sprintId)
                .orElseThrow(() -> new RuntimeException("No hay Sprint Review para este sprint"));

        return convertReviewToResponse(review);
    }

    @Transactional
    public SprintReviewResponse updateSprintReview(Long id, SprintReviewRequest request) {
        log.info("Actualizando Sprint Review con ID: {}", id);

        SprintReview review = sprintReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint Review no encontrada"));

        review.setSummary(request.getSummary());
        review.setDemoNotes(request.getDemoNotes());
        review.setFeedback(request.getFeedback());
        review.setClientComments(request.getClientComments());
        review.setAttendees(request.getAttendees());
        review.setDeliverableUrls(request.getDeliverableUrls());

        if (request.getReviewDate() != null) {
            review.setReviewDate(request.getReviewDate());
        }

        SprintReview updatedReview = sprintReviewRepository.save(review);
        log.info("Sprint Review actualizada");

        return convertReviewToResponse(updatedReview);
    }
    @Transactional(readOnly = true)
    public SprintReviewResponse getSprintReviewById(Long id) {
        log.debug("Obteniendo Sprint Review con ID: {}", id);

        SprintReview review = sprintReviewRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Sprint Review no encontrada"));

        return convertReviewToResponse(review);
    }

    @Transactional
    public ActionItemResponse updateActionItem(Long actionItemId, ActionItemRequest request) {
        log.info("Actualizando acción con ID: {}", actionItemId);

        ActionItem actionItem = actionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new RuntimeException("Acción no encontrada"));

        actionItem.setDescription(request.getDescription());
        actionItem.setStatus(request.getStatus());
        actionItem.setDueDate(request.getDueDate());

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            actionItem.setAssignedTo(assignedTo);
        } else {
            actionItem.setAssignedTo(null);
        }

        ActionItem updatedActionItem = actionItemRepository.save(actionItem);
        log.info("Acción actualizada");

        return convertActionItemToResponse(updatedActionItem);
    }

    @Transactional(readOnly = true)
    public SprintRetrospectiveResponse getRetrospectiveById(Long id) {
        log.debug("Obteniendo retrospectiva con ID: {}", id);

        SprintRetrospective retrospective = sprintRetrospectiveRepository.findByIdWithItems(id)
                .orElseThrow(() -> new RuntimeException("Retrospectiva no encontrada"));

        return convertRetrospectiveToResponse(retrospective);
    }

    @Transactional(readOnly = true)
    public SprintRetrospectiveResponse getRetrospectiveBySprintId(Long sprintId) {
        log.debug("Obteniendo retrospectiva del sprint: {}", sprintId);

        SprintRetrospective retrospective = sprintRetrospectiveRepository.findBySprintId(sprintId)
                .orElseThrow(() -> new RuntimeException("No hay retrospectiva para este sprint"));

        return convertRetrospectiveToResponse(retrospective);
    }

    @Transactional(readOnly = true)
    public List<ActionItemResponse> getActionItemsByUser(Long userId) {
        log.debug("Obteniendo acciones del usuario: {}", userId);

        return actionItemRepository.findByAssignedToId(userId).stream()
                .map(this::convertActionItemToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActionItemResponse> getPendingActionItems() {
        log.debug("Obteniendo acciones pendientes");

        return actionItemRepository.findByStatus(ActionItemStatus.PENDING).stream()
                .map(this::convertActionItemToResponse)
                .collect(Collectors.toList());
    }

    // Conversion methods

    private SprintReviewResponse convertReviewToResponse(SprintReview review) {
        return SprintReviewResponse.builder()
                .id(review.getId())
                .sprintId(review.getSprint().getId())
                .sprintName(review.getSprint().getName())
                .reviewDate(review.getReviewDate())
                .summary(review.getSummary())
                .demoNotes(review.getDemoNotes())
                .feedback(review.getFeedback())
                .clientComments(review.getClientComments())
                .attendees(review.getAttendees())
                .deliverableUrls(review.getDeliverableUrls())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private SprintRetrospectiveResponse convertRetrospectiveToResponse(SprintRetrospective retrospective) {
        List<RetrospectiveItemResponse> items = retrospective.getItems().stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());

        List<ActionItemResponse> actionItems = retrospective.getActionItems().stream()
                .map(this::convertActionItemToResponse)
                .collect(Collectors.toList());

        return SprintRetrospectiveResponse.builder()
                .id(retrospective.getId())
                .sprintId(retrospective.getSprint().getId())
                .sprintName(retrospective.getSprint().getName())
                .retrospectiveDate(retrospective.getRetrospectiveDate())
                .summary(retrospective.getSummary())
                .items(items)
                .actionItems(actionItems)
                .createdAt(retrospective.getCreatedAt())
                .updatedAt(retrospective.getUpdatedAt())
                .build();
    }

    private RetrospectiveItemResponse convertItemToResponse(RetrospectiveItem item) {
        UserDTO createdBy = UserDTO.builder()
                .id(item.getCreatedBy().getId())
                .username(item.getCreatedBy().getUsername())
                .firstName(item.getCreatedBy().getFirstName())
                .lastName(item.getCreatedBy().getLastName())
                .build();

        return RetrospectiveItemResponse.builder()
                .id(item.getId())
                .type(item.getType())
                .description(item.getDescription())
                .createdBy(createdBy)
                .createdAt(item.getCreatedAt())
                .build();
    }

    private ActionItemResponse convertActionItemToResponse(ActionItem actionItem) {
        UserDTO assignedTo = null;
        if (actionItem.getAssignedTo() != null) {
            assignedTo = UserDTO.builder()
                    .id(actionItem.getAssignedTo().getId())
                    .username(actionItem.getAssignedTo().getUsername())
                    .firstName(actionItem.getAssignedTo().getFirstName())
                    .lastName(actionItem.getAssignedTo().getLastName())
                    .build();
        }

        return ActionItemResponse.builder()
                .id(actionItem.getId())
                .description(actionItem.getDescription())
                .assignedTo(assignedTo)
                .status(actionItem.getStatus())
                .dueDate(actionItem.getDueDate())
                .createdAt(actionItem.getCreatedAt())
                .updatedAt(actionItem.getUpdatedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}