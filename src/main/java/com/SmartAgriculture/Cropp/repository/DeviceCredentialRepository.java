package com.SmartAgriculture.Cropp.repository;

import com.SmartAgriculture.Cropp.model.DeviceCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceCredentialRepository 
        extends JpaRepository<DeviceCredential, String> {
}