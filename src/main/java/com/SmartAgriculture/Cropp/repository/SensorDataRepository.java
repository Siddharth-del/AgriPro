package com.SmartAgriculture.Cropp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SmartAgriculture.Cropp.model.SensorData;
import com.SmartAgriculture.Cropp.model.User;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    Optional<SensorData> findTopByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT s FROM SensorData s WHERE s.user = :user ORDER BY s.createdAt DESC")
    List<SensorData> findTopNByUserOrderByCreatedAtDesc(@Param("user") User user, Pageable pageable);

    default List<SensorData> findTopNByUserOrderByCreatedAtDesc(User user, int limit) {
        return findTopNByUserOrderByCreatedAtDesc(user, Pageable.ofSize(limit));
    }

    @Query("SELECT s FROM SensorData s ORDER BY s.createdAt DESC")
    List<SensorData> findLatestAll(Pageable pageable);

    default Optional<SensorData> findTopOrderByCreatedAtDesc() {
        List<SensorData> results = findLatestAll(Pageable.ofSize(1));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    Optional<SensorData> findByUserUserId(Long userId);
    Optional<SensorData> findTopByUserAndDeviceIdNotNullOrderByCreatedAtDesc(User user);
    
}