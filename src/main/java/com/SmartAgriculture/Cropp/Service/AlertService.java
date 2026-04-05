package com.SmartAgriculture.Cropp.Service;

import com.SmartAgriculture.Cropp.dtos.AlertResponse;
import com.SmartAgriculture.Cropp.dtos.SoilDataRequest;

public interface AlertService {
    AlertResponse processAlert(String city, SoilDataRequest request);
}
