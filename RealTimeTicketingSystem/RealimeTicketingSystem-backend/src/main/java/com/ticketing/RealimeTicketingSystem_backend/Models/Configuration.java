package com.ticketing.RealimeTicketingSystem_backend.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Configuration class to store the configuration details
 */
@Data
@Document(collection = "configuration")

public class Configuration {
    @Id
    private String id;
    private int totalTickets; // Initial number of tickets available in the simulation.
    private int ticketReleaseRate; // Rate at which tickets are released by vendors.
    private int customerRetrievalRate; // Rate at which customers retrieve tickets
    private int maxTicketCapacity; // Maximum capacity for tickets in the system.
    private int noOfVendors; // Number of vendors in the system.
    private int noOfCustomers; // Number of customers in the system.

    /**
     * Constructor to initialize the configuration details
     * @param id (unique identifier)
     * @param totalTickets (Initial number of tickets available in the simulation)
     * @param ticketReleaseRate (Rate at which tickets are released by vendors)
     * @param customerRetrievalRate (Rate at which customers retrieve tickets)
     * @param maxTicketCapacity (Maximum capacity for tickets in the system)
     * @param noOfVendors (Number of vendors in the system)
     * @param noOfCustomers (Number of customers in the system)
     */
    public Configuration(String id,
                         int totalTickets,
                         int ticketReleaseRate,
                         int customerRetrievalRate,
                         int maxTicketCapacity,
                         int noOfVendors,
                         int noOfCustomers) {
        this.id = id;
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
        this.noOfVendors = noOfVendors;
        this.noOfCustomers = noOfCustomers;
    }
}
