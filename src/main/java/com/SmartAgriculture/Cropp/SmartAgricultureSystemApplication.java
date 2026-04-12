package com.SmartAgriculture.Cropp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
@Async
public class SmartAgricultureSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartAgricultureSystemApplication.class, args);
	}

}
