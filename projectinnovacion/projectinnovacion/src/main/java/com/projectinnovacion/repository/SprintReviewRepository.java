package com.projectinnovacion.repository;


import com.projectinnovacion.model.SprintReview;
import com.projectinnovacion.model.SprintReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SprintReviewRepository extends JpaRepository<SprintReview, Long> {

    Optional<com.projectinnovacion.model.SprintReview> findBySprintId(Long sprintId);

    @Query("SELECT sr FROM SprintReview sr LEFT JOIN FETCH sr.attendees LEFT JOIN FETCH sr.deliverableUrls WHERE sr.id = :id")
    Optional<SprintReview> findByIdWithDetails(@Param("id") Long id);
}