package com.SmartAgriculture.Cropp.Service;

import com.SmartAgriculture.Cropp.dtos.IrrigationDTO;

public interface IrrigationService {
    public String getIrrigationAlert(IrrigationDTO irrigationDTO,String city);
}
