package com.projectinnovacion.repository;


import com.projectinnovacion.model.DailyUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyUpdateRepository extends JpaRepository<DailyUpdate, Long> {

    List<DailyUpdate> findByDailyScrumId(Long dailyScrumId);

    @Query("SELECT du FROM DailyUpdate du WHERE du.dailyScrum.id = :dailyScrumId AND du.user.id = :userId")
    Optional<DailyUpdate> findByDailyScrumIdAndUserId(@Param("dailyScrumId") Long dailyScrumId, @Param("userId") Long userId);

    List<DailyUpdate> findByUserId(Long userId);
}