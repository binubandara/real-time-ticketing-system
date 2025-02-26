package com.ticketing.RealimeTicketingSystem_backend.Repo;

import com.ticketing.RealimeTicketingSystem_backend.Models.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for accessing Configuration data stored in MongoDB.
 * Extends MongoRepository to provide CRUD operations.
 */
public interface ConfigurationRepository extends MongoRepository<Configuration, String> {
    /**
     * Method to find the latest configuration details.
     * @return (Latest configuration details)
     */
    Configuration findTopByOrderByIdDesc();
}
