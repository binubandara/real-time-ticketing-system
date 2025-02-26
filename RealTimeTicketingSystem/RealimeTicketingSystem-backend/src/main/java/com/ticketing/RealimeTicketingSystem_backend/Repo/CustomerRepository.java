package com.ticketing.RealimeTicketingSystem_backend.Repo;

import com.ticketing.RealimeTicketingSystem_backend.Models.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for accessing Customer data stored in MongoDB.
 * Extends MongoRepository to provide CRUD operations.
 */
public interface CustomerRepository extends MongoRepository<Customer, String>{
    /**
     * Method to find the customer details by customerId.
     * @param customerId (Unique identifier for the customer)
     * @return (Customer details)
     */
    Optional<Customer> findByCustomerId(String customerId);
}
