package com.projectinnovacion.repository;
import com.projectinnovacion.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findByProjectIdOrderByStartDateDesc(Long projectId);

    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId AND s.active = true")
    Optional<Sprint> findActiveSprintByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId ORDER BY s.startDate DESC")
    List<Sprint> findAllByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT s FROM Sprint s LEFT JOIN FETCH s.userStories WHERE s.id = :id")
    Optional<Sprint> findByIdWithStories(@Param("id") Long id);

    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId AND s.startDate > CURRENT_TIMESTAMP ORDER BY s.startDate ASC")
    List<Sprint> findUpcomingSprintsByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId AND s.completedAt IS NOT NULL ORDER BY s.completedAt DESC")
    List<Sprint> findCompletedSprintsByProjectId(@Param("projectId") Long projectId);
}