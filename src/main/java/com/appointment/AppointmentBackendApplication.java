package com.appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppointmentBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppointmentBackendApplication.class, args);
	}

}
