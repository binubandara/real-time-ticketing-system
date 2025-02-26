package com.ticketing.RealimeTicketingSystem_backend.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Customer class to store the customer details
 */
@Data
@Document(collection = "customer_purchases")
public class Customer {
    @Id
    private String customerId; // Unique identifier for the customer
    private int totalTicketsPurchased; // Total number of tickets purchased by the customer

    /**
     * Constructor to initialize the customer details
     * @param customerId (unique identifier)
     * @param totalTicketsPurchased (Total number of tickets purchased by the customer)
     */
    public Customer(String customerId, int totalTicketsPurchased) {
        this.customerId = customerId;
        this.totalTicketsPurchased = totalTicketsPurchased;
    }
}