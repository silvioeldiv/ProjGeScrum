package com.projectinnovacion.repository;


import com.projectinnovacion.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByName(String name);

    @Query("SELECT p FROM Project p WHERE p.active = true")
    List<Project> findAllActive();

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.members WHERE p.id = :id")
    Optional<Project> findByIdWithMembers(Long id);

    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN p.members m WHERE m.id = :userId OR p.productOwner.id = :userId OR p.scrumMaster.id = :userId")
    List<Project> findByUserInvolved(Long userId);
}
