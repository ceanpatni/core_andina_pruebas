package com.example.backProcesamientoStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // habilita @Scheduled
@EnableAsync       // habilita ejecución asíncrona
public class BackProcesamientoStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackProcesamientoStreamApplication.class, args);
	}

}
