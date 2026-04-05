package com.SmartAgriculture.Cropp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SmartAgriculture.Cropp.model.AppRole;
import com.SmartAgriculture.Cropp.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
