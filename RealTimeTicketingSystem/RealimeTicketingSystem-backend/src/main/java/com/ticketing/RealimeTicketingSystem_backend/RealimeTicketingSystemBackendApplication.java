package com.ticketing.RealimeTicketingSystem_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class to start the Spring Boot application.
 */
@SpringBootApplication
public class RealimeTicketingSystemBackendApplication {

	/**
	 * Default constructor for the application class.
	 * This constructor is invoked implicitly when the class is loaded.
	 */
	public RealimeTicketingSystemBackendApplication() {
		// Default constructor
	}

	/**
	 * Main method to start the Spring Boot application.
	 * @param args Command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(RealimeTicketingSystemBackendApplication.class, args);
	}
}
