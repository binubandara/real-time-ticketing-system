package com.ticketing.RealimeTicketingSystem_backend.Components;

import com.ticketing.RealimeTicketingSystem_backend.Models.Configuration;
import com.ticketing.RealimeTicketingSystem_backend.Repo.ConfigurationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component used to initialize the default configuration for the application.
 */
@Component
public class DefaultConfig implements CommandLineRunner {

    private final ConfigurationRepository configRepository;

    /**
     * Constructor for injecting the ConfigurationRepository.
     * @param configRepository  (Repository managing configuration data)
     */
    public DefaultConfig(ConfigurationRepository configRepository) {
        this.configRepository = configRepository;
    }

    /**
     * Runs at application startup to initialize a  default configuration for the application.
     * If no configuration exists in the database, a default configuration is created and saved.
     * If a configuration already exists, the default initialization is skipped.
     * @param args  (Command line arguments)
     */
    @Override
    public void run(String... args) {

        if (configRepository.count() == 0) {

            Configuration defaultConfig = new Configuration(
                    null, // No ID is set, MongoDB will generate one.
                    50,   // Default total tickets
                    5,    // Default ticket release rate
                    3,   // Default customer retrieval rate
                    500, // Default max ticket capacity
                    5,   // Default number of vendors
                    7  // Default number of customers
            );


            configRepository.save(defaultConfig);
            System.out.println("Default configuration saved to MongoDB.");
        } else {
            System.out.println("Configuration already exists. Skipping default initialization.");
        }
    }
}
