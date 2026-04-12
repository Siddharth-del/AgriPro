package com.SmartAgriculture.Cropp.service.disease;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.SmartAgriculture.Cropp.dtos.ai.AdvisoryResponse;
import com.SmartAgriculture.Cropp.dtos.disease.DiseaseDetectionResponse;
import com.SmartAgriculture.Cropp.dtos.disease.DiseasePredictionResponse;
import com.SmartAgriculture.Cropp.exception.InvalidImageException;
import com.SmartAgriculture.Cropp.model.DiseaseDetection;
import com.SmartAgriculture.Cropp.repository.DiseaseDetectionRepository;
import com.SmartAgriculture.Cropp.repository.UserRepository;
import com.SmartAgriculture.Cropp.service.ai.AiAdvisoryService;
import com.SmartAgriculture.Cropp.service.ai.MlPredictionService;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class DiseaseDetectionServiceImpl implements DiseaseDetectionService {

    private final DiseaseDetectionRepository diseaseRepository;
    private final MlPredictionService mlPredictionService;
    private final AiAdvisoryService aiAdvisoryService;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ImageValidationService imageValidationService;

    @Value("${project.image}")
    private String imagePath;

    @PostConstruct
    public void init() throws IOException {
        Path path = Paths.get(imagePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Created image directory: {}", path.toAbsolutePath());
        }
    }

    @Override
    @Transactional
    public DiseaseDetectionResponse detectdisease(MultipartFile image) throws IOException {
        String fileName = null;

        try {
            imageValidationService.validate(image);

            fileName = fileService.uploadImage(imagePath, image);
            File savedFile = new File(imagePath + File.separator + fileName);

            log.info("Image saved: {}", savedFile.getAbsolutePath());

            DiseasePredictionResponse result = mlPredictionService.detectDisease(savedFile);

            log.info("Disease detected: {} with confidence: {}", result.getDiseaseName(), result.getConfidence());

            AdvisoryResponse advisory = aiAdvisoryService.generateDiseaseAdvisory(result.getDiseaseName());

            DiseaseDetection disease = new DiseaseDetection();
            disease.setImagePath(fileName);
            disease.setDiseaseName(result.getDiseaseName());
            disease.setConfidenceScore(result.getConfidence());
            disease.setExplanation(advisory.getExplanation());
            disease.setPesticideSuggestion(advisory.getPesticideRecommendation());
            disease.setFertilizerSuggestion(advisory.getFertilizerRecommendation());

            DiseaseDetection saved = diseaseRepository.save(disease);

            DiseaseDetectionResponse response = new DiseaseDetectionResponse();
            response.setDiseaseId(saved.getDiseaseId());
            response.setCreatedAt(saved.getCreatedAt());
            response.setPredicted(List.of(result));
            response.setExplanation(advisory.getExplanation());
            response.setFertilizerSuggestion(advisory.getFertilizerRecommendation());
            response.setPesticideSuggestion(advisory.getPesticideRecommendation());

            return response;

        } catch (InvalidImageException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error detecting disease: {}", e.getMessage());
            if (fileName != null) {
                try {
                    fileService.deleteImage(imagePath, fileName);
                } catch (IOException cleanupError) {
                    log.warn("Failed to cleanup file: {}", fileName);
                }
            }
            throw e;
        }
    }

    @Override
    public List<DiseaseDetectionResponse> getDiseaseByName(String diseaseName) {
        List<DiseaseDetection> diseases = diseaseRepository.findByDiseaseNameIgnoreCase(diseaseName);

        if (diseases.isEmpty()) {
            return Collections.emptyList();
        }

        return diseases.stream()
                .map(disease -> {
                    DiseaseDetectionResponse dto = modelMapper.map(disease, DiseaseDetectionResponse.class);
                    dto.setPredicted(List.of(modelMapper.map(disease, DiseasePredictionResponse.class)));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}