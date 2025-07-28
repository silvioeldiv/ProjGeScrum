package com.projectinnovacion.repository;
import com.projectinnovacion.model.UserStory;
import com.projectinnovacion.model.enums.StoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {

    @Query("SELECT us FROM UserStory us WHERE us.project.id = :projectId ORDER BY us.orderIndex ASC, us.priority DESC")
    List<UserStory> findByProjectIdOrderByOrderIndexAndPriority(@Param("projectId") Long projectId);

    List<UserStory> findByProjectIdAndStatus(Long projectId, StoryStatus status);

    List<UserStory> findByAssigneeId(Long assigneeId);

    @Query("SELECT us FROM UserStory us WHERE us.project.id = :projectId AND us.status = 'BACKLOG' ORDER BY us.orderIndex ASC, us.priority DESC")
    List<UserStory> findBacklogByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT MAX(us.orderIndex) FROM UserStory us WHERE us.project.id = :projectId")
    Integer findMaxOrderIndexByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT us FROM UserStory us WHERE us.project.id = :projectId AND us.status IN :statuses")
    List<UserStory> findByProjectIdAndStatusIn(@Param("projectId") Long projectId, @Param("statuses") List<StoryStatus> statuses);
}