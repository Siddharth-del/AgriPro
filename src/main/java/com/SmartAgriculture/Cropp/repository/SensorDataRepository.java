package com.SmartAgriculture.Cropp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SmartAgriculture.Cropp.model.SensorData;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData,Long>  {
    
}
