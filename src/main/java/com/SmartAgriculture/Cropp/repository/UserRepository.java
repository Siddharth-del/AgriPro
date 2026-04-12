package com.SmartAgriculture.Cropp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SmartAgriculture.Cropp.model.AppRole;
import com.SmartAgriculture.Cropp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

   Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :role")
    List<User> findByRole(@Param("role") AppRole role);

    Optional<User> findByEmail(String email);
    Optional<User> findByDeviceApiKey(String deviceApiKey);
    
}
