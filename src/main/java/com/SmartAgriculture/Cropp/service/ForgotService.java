package com.SmartAgriculture.Cropp.service;

import com.SmartAgriculture.Cropp.dtos.ResetPasswordDTO;

public interface  ForgotService {
    String generateOtp(String email);
    boolean verifyOtp(String email,String otp);

    String resetPassword(String email,ResetPasswordDTO request);

}
