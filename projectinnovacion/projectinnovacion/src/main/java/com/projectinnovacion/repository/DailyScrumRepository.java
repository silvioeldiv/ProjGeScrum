package com.projectinnovacion.repository;


import com.projectinnovacion.model.DailyScrum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyScrumRepository extends JpaRepository<DailyScrum, Long> {

    @Query("SELECT ds FROM DailyScrum ds WHERE ds.sprint.id = :sprintId ORDER BY ds.meetingDate DESC")
    List<DailyScrum> findBySprintIdOrderByMeetingDateDesc(@Param("sprintId") Long sprintId);

    @Query("SELECT ds FROM DailyScrum ds WHERE ds.sprint.id = :sprintId AND DATE(ds.meetingDate) = DATE(:date)")
    Optional<DailyScrum> findBySprintIdAndMeetingDate(@Param("sprintId") Long sprintId, @Param("date") LocalDateTime date);

    @Query("SELECT ds FROM DailyScrum ds LEFT JOIN FETCH ds.updates WHERE ds.id = :id")
    Optional<DailyScrum> findByIdWithUpdates(@Param("id") Long id);

    @Query("SELECT ds FROM DailyScrum ds WHERE ds.sprint.id = :sprintId AND ds.meetingDate BETWEEN :startDate AND :endDate ORDER BY ds.meetingDate")
    List<DailyScrum> findBySprintIdAndDateRange(
            @Param("sprintId") Long sprintId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}