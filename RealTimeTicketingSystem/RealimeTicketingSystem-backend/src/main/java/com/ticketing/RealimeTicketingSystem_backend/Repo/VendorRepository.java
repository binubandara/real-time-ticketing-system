package com.ticketing.RealimeTicketingSystem_backend.Repo;

import com.ticketing.RealimeTicketingSystem_backend.Models.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for accessing Vendor data stored in MongoDB.
 * Extends MongoRepository to provide CRUD operations.
 */
public interface VendorRepository extends MongoRepository<Vendor, String> {
}