package com.SmartAgriculture.Cropp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SmartAgriculture.Cropp.model.CropRecommendation;

@Repository
public interface CropRepository extends JpaRepository<CropRecommendation, Long> {

    List<CropRecommendation> findByCropNameIgnoreCase(String name);

    @Query("SELECT cp FROM CropRecommendation cp")
    List<CropRecommendation> findWithCrop();

}
