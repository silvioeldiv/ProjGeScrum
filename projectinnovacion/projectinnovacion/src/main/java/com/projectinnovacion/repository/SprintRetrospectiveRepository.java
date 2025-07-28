package com.projectinnovacion.repository;


import com.projectinnovacion.model.SprintRetrospective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SprintRetrospectiveRepository extends JpaRepository<SprintRetrospective, Long> {

    Optional<SprintRetrospective> findBySprintId(Long sprintId);

    @Query("SELECT sr FROM SprintRetrospective sr LEFT JOIN FETCH sr.items LEFT JOIN FETCH sr.actionItems WHERE sr.id = :id")
    Optional<SprintRetrospective> findByIdWithItems(@Param("id") Long id);
}