package com.ticketing.RealimeTicketingSystem_backend.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Vendor class to store vendor's ticket release details.
 */
@Data
@Document(collection = "vendor_releases")
public class Vendor {
    @Id
    private String vendorId; // Unique identifier for the vendor
    private int totalTicketsReleased; // Total number of tickets released by the vendor

    /**
     * Default constructor for Vendor.
     * Initializes the vendor details with default values.
     */
    public Vendor() {
        // Default constructor
    }
}