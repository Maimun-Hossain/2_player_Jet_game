package com.example.jetgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Jetgame Spring Boot application.
 * This class uses the @SpringBootApplication annotation to enable auto-configuration,
 * component scanning, and allow the application to be run as a standalone Java application.
 */
@SpringBootApplication
public class JetgameApplication {

	public static void main(String[] args) {
		SpringApplication.run(JetgameApplication.class, args);
	}

}
