package com.SmartAgriculture.Cropp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SmartAgriculture.Cropp.model.DiseaseDetection;

@Repository
public interface DiseaseDetectionRepository extends JpaRepository<DiseaseDetection,Long>{

  @Query("SELECT d FROM DiseaseDetection d WHERE LOWER(REPLACE(REPLACE(d.diseaseName, '_', ''), ' ', '')) = LOWER(REPLACE(REPLACE(:name, '_', ''), ' ', ''))")
List<DiseaseDetection> findByDiseaseNameIgnoreCase(@Param("name") String name);


    
}
