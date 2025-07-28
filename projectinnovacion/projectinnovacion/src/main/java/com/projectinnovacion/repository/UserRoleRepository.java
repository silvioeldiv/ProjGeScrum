package com.projectinnovacion.repository;

import com.projectinnovacion.model.UserRole;
import com.projectinnovacion.model.enums.ERole;
import com.projectinnovacion.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

   // Optional<UserRole> findByName(RoleType name);

    //boolean existsByName(RoleType name);
    Optional<UserRole> findByName(ERole name);

    boolean existsByName(RoleType roleType);

    boolean existsByName(ERole eRole);
}