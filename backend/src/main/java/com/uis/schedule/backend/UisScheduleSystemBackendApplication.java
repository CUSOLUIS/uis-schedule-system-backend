package com.uis.schedule.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UisScheduleSystemBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(UisScheduleSystemBackendApplication.class, args);
	}
}
