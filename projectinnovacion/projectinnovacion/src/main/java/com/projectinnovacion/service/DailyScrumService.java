package com.projectinnovacion.service;

import com.projectinnovacion.dto.request.DailyScrumRequest;
import com.projectinnovacion.dto.request.DailyUpdateRequest;
import com.projectinnovacion.dto.response.DailyScrumResponse;
import com.projectinnovacion.dto.response.DailyUpdateResponse;
import com.projectinnovacion.dto.response.UserDTO;
import com.projectinnovacion.model.DailyScrum;
import com.projectinnovacion.model.DailyUpdate;
import com.projectinnovacion.model.Sprint;
import com.projectinnovacion.model.User;
import com.projectinnovacion.repository.DailyScrumRepository;
import com.projectinnovacion.repository.DailyUpdateRepository;
import com.projectinnovacion.repository.SprintRepository;
import com.projectinnovacion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyScrumService {

    private final DailyScrumRepository dailyScrumRepository;
    private final DailyUpdateRepository dailyUpdateRepository;
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;

    @Transactional
    public DailyScrumResponse createDailyScrum(DailyScrumRequest request) {
        log.info("Creando Daily Scrum para sprint: {}", request.getSprintId());

        Sprint sprint = sprintRepository.findById(request.getSprintId())
                .orElseThrow(() -> new RuntimeException("Sprint no encontrado"));

        if (!sprint.getActive()) {
            throw new RuntimeException("El sprint no está activo");
        }

        LocalDateTime meetingDate = request.getMeetingDate() != null ?
                request.getMeetingDate() : LocalDateTime.now();

        // Verificar si ya existe un daily scrum para esta fecha
        dailyScrumRepository.findBySprintIdAndMeetingDate(sprint.getId(), meetingDate)
                .ifPresent(ds -> {
                    throw new RuntimeException("Ya existe un Daily Scrum para esta fecha");
                });

        DailyScrum dailyScrum = DailyScrum.builder()
                .sprint(sprint)
                .meetingDate(meetingDate)
                .notes(request.getNotes())
                .build();

        DailyScrum savedDailyScrum = dailyScrumRepository.save(dailyScrum);
        log.info("Daily Scrum creado con ID: {}", savedDailyScrum.getId());

        return convertToResponse(savedDailyScrum);
    }

    @Transactional
    public DailyUpdateResponse addDailyUpdate(Long dailyScrumId, DailyUpdateRequest request) {
        log.info("Agregando actualización diaria al Daily Scrum: {}", dailyScrumId);

        DailyScrum dailyScrum = dailyScrumRepository.findById(dailyScrumId)
                .orElseThrow(() -> new RuntimeException("Daily Scrum no encontrado"));

        User currentUser = getCurrentUser();

        // Verificar si el usuario ya tiene una actualización para este daily scrum
        dailyUpdateRepository.findByDailyScrumIdAndUserId(dailyScrumId, currentUser.getId())
                .ifPresent(update -> {
                    throw new RuntimeException("Ya has agregado tu actualización a este Daily Scrum");
                });

        DailyUpdate dailyUpdate = DailyUpdate.builder()
                .dailyScrum(dailyScrum)
                .user(currentUser)
                .yesterdayWork(request.getYesterdayWork())
                .todayPlan(request.getTodayPlan())
                .blockers(request.getBlockers())
                .build();

        DailyUpdate savedUpdate = dailyUpdateRepository.save(dailyUpdate);
        log.info("Actualización diaria agregada con ID: {}", savedUpdate.getId());

        return convertUpdateToResponse(savedUpdate);
    }

    @Transactional(readOnly = true)
    public DailyScrumResponse getDailyScrumById(Long id) {
        log.debug("Obteniendo Daily Scrum con ID: {}", id);

        DailyScrum dailyScrum = dailyScrumRepository.findByIdWithUpdates(id)
                .orElseThrow(() -> new RuntimeException("Daily Scrum no encontrado"));

        return convertToResponse(dailyScrum);
    }

    @Transactional(readOnly = true)
    public List<DailyScrumResponse> getDailyScrumsBySprintId(Long sprintId) {
        log.debug("Obteniendo Daily Scrums del sprint: {}", sprintId);

        return dailyScrumRepository.findBySprintIdOrderByMeetingDateDesc(sprintId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DailyScrumResponse getTodayDailyScrum(Long sprintId) {
        log.debug("Obteniendo Daily Scrum de hoy para sprint: {}", sprintId);

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        DailyScrum dailyScrum = dailyScrumRepository.findBySprintIdAndMeetingDate(sprintId, startOfDay)
                .orElseThrow(() -> new RuntimeException("No hay Daily Scrum para hoy"));

        return convertToResponse(dailyScrum);
    }

    @Transactional
    public DailyUpdateResponse updateDailyUpdate(Long updateId, DailyUpdateRequest request) {
        log.info("Actualizando actualización diaria con ID: {}", updateId);

        DailyUpdate dailyUpdate = dailyUpdateRepository.findById(updateId)
                .orElseThrow(() -> new RuntimeException("Actualización no encontrada"));

        User currentUser = getCurrentUser();
        if (!dailyUpdate.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No puedes actualizar la actualización de otro usuario");
        }

        dailyUpdate.setYesterdayWork(request.getYesterdayWork());
        dailyUpdate.setTodayPlan(request.getTodayPlan());
        dailyUpdate.setBlockers(request.getBlockers());

        DailyUpdate updatedUpdate = dailyUpdateRepository.save(dailyUpdate);
        log.info("Actualización diaria actualizada");

        return convertUpdateToResponse(updatedUpdate);
    }

    @Transactional(readOnly = true)
    public List<DailyScrumResponse> getDailyScrumsByDateRange(Long sprintId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Obteniendo Daily Scrums entre {} y {}", startDate, endDate);

        return dailyScrumRepository.findBySprintIdAndDateRange(sprintId, startDate, endDate)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private DailyScrumResponse convertToResponse(DailyScrum dailyScrum) {
        List<DailyUpdateResponse> updates = dailyScrum.getUpdates().stream()
                .map(this::convertUpdateToResponse)
                .collect(Collectors.toList());

        return DailyScrumResponse.builder()
                .id(dailyScrum.getId())
                .sprintId(dailyScrum.getSprint().getId())
                .sprintName(dailyScrum.getSprint().getName())
                .meetingDate(dailyScrum.getMeetingDate())
                .notes(dailyScrum.getNotes())
                .updates(updates)
                .createdAt(dailyScrum.getCreatedAt())
                .updatedAt(dailyScrum.getUpdatedAt())
                .build();
    }

    private DailyUpdateResponse convertUpdateToResponse(DailyUpdate update) {
        UserDTO userDTO = UserDTO.builder()
                .id(update.getUser().getId())
                .username(update.getUser().getUsername())
                .email(update.getUser().getEmail())
                .firstName(update.getUser().getFirstName())
                .lastName(update.getUser().getLastName())
                .build();

        return DailyUpdateResponse.builder()
                .id(update.getId())
                .user(userDTO)
                .yesterdayWork(update.getYesterdayWork())
                .todayPlan(update.getTodayPlan())
                .blockers(update.getBlockers())
                .createdAt(update.getCreatedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}