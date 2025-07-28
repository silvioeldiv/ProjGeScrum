package com.projectinnovacion.controller;


import com.projectinnovacion.dto.request.DailyScrumRequest;
import com.projectinnovacion.dto.request.DailyUpdateRequest;
import com.projectinnovacion.dto.response.DailyScrumResponse;
import com.projectinnovacion.dto.response.DailyUpdateResponse;
import com.projectinnovacion.service.DailyScrumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/daily-scrum")
public class DailyScrumController {

    private final DailyScrumService dailyScrumService;

    @PostMapping
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<DailyScrumResponse> createDailyScrum(@Valid @RequestBody DailyScrumRequest request) {
        log.info("Creando Daily Scrum");
        DailyScrumResponse response = dailyScrumService.createDailyScrum(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{dailyScrumId}/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DailyUpdateResponse> addDailyUpdate(
            @PathVariable Long dailyScrumId,
            @Valid @RequestBody DailyUpdateRequest request) {
        log.info("Agregando actualización diaria");
        DailyUpdateResponse response = dailyScrumService.addDailyUpdate(dailyScrumId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DailyScrumResponse> getDailyScrumById(@PathVariable Long id) {
        log.info("Obteniendo Daily Scrum con ID: {}", id);
        DailyScrumResponse response = dailyScrumService.getDailyScrumById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sprint/{sprintId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DailyScrumResponse>> getDailyScrumsBySprintId(@PathVariable Long sprintId) {
        log.info("Obteniendo Daily Scrums del sprint: {}", sprintId);
        List<DailyScrumResponse> response = dailyScrumService.getDailyScrumsBySprintId(sprintId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sprint/{sprintId}/today")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DailyScrumResponse> getTodayDailyScrum(@PathVariable Long sprintId) {
        log.info("Obteniendo Daily Scrum de hoy para sprint: {}", sprintId);
        DailyScrumResponse response = dailyScrumService.getTodayDailyScrum(sprintId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{updateId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DailyUpdateResponse> updateDailyUpdate(
            @PathVariable Long updateId,
            @Valid @RequestBody DailyUpdateRequest request) {
        log.info("Actualizando actualización diaria con ID: {}", updateId);
        DailyUpdateResponse response = dailyScrumService.updateDailyUpdate(updateId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sprint/{sprintId}/range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DailyScrumResponse>> getDailyScrumsByDateRange(
            @PathVariable Long sprintId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Obteniendo Daily Scrums entre {} y {}", startDate, endDate);
        List<DailyScrumResponse> response = dailyScrumService.getDailyScrumsByDateRange(sprintId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}