
package com.SmartAgriculture.Cropp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SmartAgriculture.Cropp.model.FarmerProfile;
import com.SmartAgriculture.Cropp.model.User;

@Repository
public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {

    boolean existsByUser(User user);

    boolean existsByContactNumber(String contactNumber);

    Optional<FarmerProfile> findByUser(User user);

    @Modifying
    @Query("DELETE FROM FarmerProfile fp WHERE fp.user = :user")
    void deleteByUser(@Param("user") User user);

    boolean existsByUser_UserId(Long userId);

    @Modifying
    @Query(value = "DELETE FROM farmer_profile WHERE user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") Long userId);

    @Query("SELECT fp FROM FarmerProfile fp JOIN FETCH fp.user")
    List<FarmerProfile> findWithUser();

    @Query("SELECT fp FROM FarmerProfile fp JOIN FETCH fp.user u WHERE u.userId = :userId")
    Optional<FarmerProfile> findByUserId(@Param("userId") Long userId);
}