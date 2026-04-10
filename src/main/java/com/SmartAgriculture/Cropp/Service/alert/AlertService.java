package com.SmartAgriculture.Cropp.service.alert;

import com.SmartAgriculture.Cropp.dtos.alert.AlertResponse;
import com.SmartAgriculture.Cropp.dtos.sensor.SoilDataRequest;

public interface AlertService {
    String processAlert(String city, SoilDataRequest request);
}
