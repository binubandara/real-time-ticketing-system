package com.ticketing.RealimeTicketingSystem_backend.Models;

import lombok.Data;

/**
 * TicketStatus class is used to store the status of the tickets
 */
@Data
public class TicketStatus {
    private int remainingTickets; // Tickets still available for customers.
    private int totalTicketsReleased; // Total tickets released so far.
    private boolean allTicketsReleased; // Total tickets released so far.
    private boolean allTicketsSold; // Whether all tickets have been sold.

    /**
     * Default constructor for TicketStatus.
     * Initializes the ticket status with default values.
     */
    public TicketStatus() {
        // Default constructor
    }
}
