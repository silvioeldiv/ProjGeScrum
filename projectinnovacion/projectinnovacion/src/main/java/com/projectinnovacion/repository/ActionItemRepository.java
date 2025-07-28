package com.projectinnovacion.repository;


import com.projectinnovacion.model.ActionItem;
import com.projectinnovacion.model.enums.ActionItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    List<ActionItem> findByAssignedToId(Long userId);

    List<ActionItem> findByStatus(ActionItemStatus status);

    List<ActionItem> findByRetrospectiveId(Long retrospectiveId);
}