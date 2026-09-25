package com.SmartAgriculture.Cropp.service.alert.Crop;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SmartAgriculture.Cropp.dtos.WeatherResponse;
import com.SmartAgriculture.Cropp.dtos.ai.AdvisoryResponse;
import com.SmartAgriculture.Cropp.dtos.crop.CropRecommendationResponse;
import com.SmartAgriculture.Cropp.dtos.crop.CropRequestDTO;
import com.SmartAgriculture.Cropp.exception.ResourceNotFoundException;
import com.SmartAgriculture.Cropp.model.CropRecommendation;
import com.SmartAgriculture.Cropp.model.SensorData;
import com.SmartAgriculture.Cropp.repository.CropRepository;
import com.SmartAgriculture.Cropp.repository.SensorDataRepository;
import com.SmartAgriculture.Cropp.service.WeatherService;
import com.SmartAgriculture.Cropp.service.ai.AiAdvisoryService;
import com.SmartAgriculture.Cropp.service.ai.MlPredictionService;
import com.SmartAgriculture.Cropp.utils.AuthUtil;

import io.jsonwebtoken.lang.Collections;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CropRecommendationServiceImpl implements CropRecommendationService {

        private final SensorDataRepository sensorDataRepository;
        private final CropRepository cropRepository;
        private final MlPredictionService mlPredictionService;
        private final AiAdvisoryService aiAdvisoryService;
        private final ModelMapper modelMapper;
        private final WeatherService weatherService;
        private final AuthUtil authUtil;

        @Override
        @Transactional
        public CropRecommendationResponse recommendCrop(CropRequestDTO request) {
                WeatherResponse weather = weatherService.getWeatherCity(request.getLocation());
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
                        throw new RuntimeException("User not authenticated");
                }
                double temp = weather.getMain().getTemp();
                double humi = weather.getMain().getHumidity();

                SensorData sensorData = new SensorData();
                sensorData.setHumidity(humi);
                sensorData.setNitrogen(request.getNitrogen());
                sensorData.setPhosphorus(request.getPhosphorus());
                sensorData.setPotassium(request.getPotassium());
                sensorData.setRainfall(request.getRainfall());
                sensorData.setTemperature(temp);
                sensorData.setPh(request.getPh());
                sensorData.setUser(authUtil.loggedInUser());

                sensorDataRepository.save(sensorData);

                CropRecommendationResponse result = mlPredictionService.predictCrop(sensorData);

                AdvisoryResponse advisory = aiAdvisoryService.generateCropAdvisory(
                                result.getCropName(),
                                sensorData);

                CropRecommendationResponse response = new CropRecommendationResponse();

                response.setCropName(result.getCropName());
                response.setCropConfidence(result.getCropConfidence());
                response.setExplanation(advisory.getExplanation());

                CropRecommendation crop = new CropRecommendation();
                crop.setCropName(response.getCropName());
                crop.setExplanation(response.getExplanation());
                crop.setConfidenceScore(response.getCropConfidence());
               
                crop.setSensorData(sensorData);
                crop.setLocation(request.getLocation());
                CropRecommendation save = cropRepository.save(crop);

                response.setCropId(save.getCropId());

                return response;
        }

        @Override
        public List<CropRecommendationResponse> getCropByName(String name) {
                List<CropRecommendation> crops = cropRepository.findByCropNameIgnoreCase(name);
                if (crops.isEmpty()) {
                        return Collections.emptyList();
                }
                List<CropRecommendationResponse> response = crops.stream()
                                .map(crop -> modelMapper.map(crop, CropRecommendationResponse.class))
                                .collect(Collectors.toList());

                return response;
        }

        @Override
        public List<CropRecommendationResponse> getAllCrop() {
                List<CropRecommendation> crops = cropRepository.findWithCrop();
                if (crops.isEmpty()) {
                     return Collections.emptyList();
                }

                List<CropRecommendationResponse> response = crops.stream()
                                .map(crop -> modelMapper.map(crop, CropRecommendationResponse.class)).toList();
                return response;
        }

}
