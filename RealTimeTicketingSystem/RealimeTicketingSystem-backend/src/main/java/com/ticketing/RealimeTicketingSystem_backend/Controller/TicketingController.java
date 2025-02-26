package com.ticketing.RealimeTicketingSystem_backend.Controller;

import com.ticketing.RealimeTicketingSystem_backend.Models.Configuration;
import com.ticketing.RealimeTicketingSystem_backend.Repo.ConfigurationRepository;
import com.ticketing.RealimeTicketingSystem_backend.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller that handles API endpoints for the Real-Time Ticketing System.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class TicketingController {

    private final TicketService ticketingService;
    private final ConfigurationRepository configRepository;

    /**
     * Constructor for TicketingController
     * @param ticketingService (handling ticket simulation logic)
     * @param configRepository (managing configurations in the database)
     */
    @Autowired
    public TicketingController(TicketService ticketingService,
                               ConfigurationRepository configRepository) {
        this.ticketingService = ticketingService;
        this.configRepository = configRepository;
    }

    /**
     * Endpoint to save configuration in the database.
     * @param config (Configuration object to be saved)
     * @return ResponseEntity with saved Configuration object
     */
    @PostMapping("/configuration")
    public ResponseEntity<Configuration> saveConfiguration(@RequestBody Configuration config) {
        Configuration savedConfig = configRepository.save(config);
        return ResponseEntity.ok(savedConfig);
    }

    /**
     * Endpoint to get the latest configuration from the database.
     * @return ResponseEntity with the latest Configuration object
     */
    @GetMapping("/configuration")
    public ResponseEntity<Configuration> getConfiguration() {
        Configuration config = configRepository.findTopByOrderByIdDesc();
        return config != null ? ResponseEntity.ok(config) : ResponseEntity.notFound().build();
    }

    /**
     * Starts the simulation using the latest configuration
     * Gets the latest configuration from the database.
     * Starts the simulation using the latest configuration.
     * Else, returns bad request if no configuration is found.
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/simulation/start")
    public ResponseEntity<Void> startSimulation() {
        Configuration config = configRepository.findTopByOrderByIdDesc();
        if (config == null) {
            return ResponseEntity.badRequest().build();
        }
        ticketingService.startSimulation(config);
        return ResponseEntity.ok().build();
    }

    /**
     * Stops the simulation
     * Stops the simulation by setting isRunning to false.
     * @return ResponseEntity indicating success.
     */
    @PostMapping("/simulation/stop")
    public ResponseEntity<Void> stopSimulation() {
        ticketingService.stopSimulation();
        return ResponseEntity.ok().build();
    }
}
