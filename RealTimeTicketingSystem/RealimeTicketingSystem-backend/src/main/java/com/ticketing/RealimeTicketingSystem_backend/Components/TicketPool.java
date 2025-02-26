package com.ticketing.RealimeTicketingSystem_backend.Components;

import com.ticketing.RealimeTicketingSystem_backend.Models.SystemLogger;
import com.ticketing.RealimeTicketingSystem_backend.Models.Configuration;
import com.ticketing.RealimeTicketingSystem_backend.Models.TicketStatus;
import com.ticketing.RealimeTicketingSystem_backend.Services.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


/**
 * TicketPool class to manage the tickets in the system.
 * For adding and removing tickets,
 * and keeping track of the total tickets released and sold.
 * Provides synchronized methods to add and remove tickets.
 */
@Component
public class TicketPool {

    private final List<Integer> tickets = Collections.synchronizedList(new ArrayList<>());
    private int maxCapacity;
    private int totalTicketsReleased = 0;
    private int totalTicketsSold = 0;
    private static final Logger logger = Logger.getLogger(TicketPool.class.getName());
    private boolean allTicketsReleased = false;
    private boolean allTicketsSold = false;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Method to enable websocket messaging.
     * @param messagingTemplate (messaging template for real-time updates)
     */
    public TicketPool(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Method to initialize the ticket pool with the user configuration.
     * Clears the existing tickets and adds the initial tickets.
     * @param config (user configuration)
     */
    public void initialize(Configuration config) {
        synchronized (this) {
            tickets.clear();
            totalTicketsReleased = 0;
            totalTicketsSold = 0;
            allTicketsReleased = false;
            allTicketsSold = false;

            this.maxCapacity = config.getMaxTicketCapacity();
            int initialTickets = config.getTotalTickets();

            for (int i = 0; i < initialTickets && i < maxCapacity; i++) {
                tickets.add(1);
                totalTicketsReleased++;
            }

            logStatus("Initialized ticket pool with " + tickets.size() +
                    " tickets. Maximum capacity: " + maxCapacity, "INFO");
        }
    }

    /**
     * Method to add tickets to the pool from a vendor.
     * Checks if the pool has reached the maximum capacity,
     * and stops vendor release.
     * @param vendorId (vendor identifier)
     * @param numTickets (number of tickets to add)
     * @return (true if tickets are added, false if not)
     */
    public synchronized boolean addTickets(String vendorId, int numTickets) {
        if (allTicketsReleased || allTicketsSold) {
            return false;
        }

        int remainingCapacity = maxCapacity - totalTicketsReleased;
        if (remainingCapacity <= 0) {
            allTicketsReleased = true;
            logStatus("Maximum ticket capacity reached. No more tickets can be released.", "INFO");
            return false;
        }

        int ticketsToAdd = Math.min(numTickets, remainingCapacity);

        for (int i = 0; i < ticketsToAdd; i++) {
            tickets.add(1);
        }
        totalTicketsReleased += ticketsToAdd;

        if (totalTicketsReleased >= maxCapacity) {
            allTicketsReleased = true;
            logStatus("All " + maxCapacity + " tickets have been released. Vendors will stop releasing tickets.", "INFO");
        }

        logStatus("Vendor " + vendorId + " added " + ticketsToAdd +
                " tickets. Pool size: " + tickets.size() +
                ". Total released: " + totalTicketsReleased + "/" + maxCapacity, "INFO");

        notifyAll();
        return true;
    }

    /**
     * Method to remove tickets from the pool for a customer.
     * Checks if all tickets are sold, and returns false.
     * Checks if there are enough tickets and all have been released,
     * and returns false.
     * @param customerId (customer identifier)
     * @param numTickets (number of tickets to remove)
     * @return (true if tickets are removed, false if not)
     */
    public synchronized boolean removeTickets(String customerId, int numTickets) {
        if (totalTicketsSold >= maxCapacity || (tickets.size() < numTickets && allTicketsReleased)) {
            if (!allTicketsSold) {
                allTicketsSold = true;
                tickets.clear();
                logStatus("All tickets have been sold. Stopping the system.", "INFO");
                TicketService.isRunning = false;
                notifyStatus();
            }
            return false;
        }

        if (totalTicketsSold + numTickets > maxCapacity) {
            return false;
        }

        while (tickets.size() < numTickets && !allTicketsSold && !allTicketsReleased) {
            try {
                logStatus("Pool has fewer than " + numTickets +
                        " tickets. Customer " + customerId + " is waiting.", "WARNING");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        if (tickets.size() < numTickets || totalTicketsSold + numTickets > maxCapacity) {
            return false;
        }

        for (int i = 0; i < numTickets; i++) {
            tickets.remove(tickets.size() - 1);
        }
        totalTicketsSold += numTickets;

        if (totalTicketsSold >= maxCapacity) {
            allTicketsSold = true;
            tickets.clear();
            logStatus("All tickets have been sold. Stopping the system.", "INFO");
            TicketService.isRunning = false;
        }

        logStatus("Customer " + customerId + " purchased " + numTickets +
                " tickets. Pool size: " + tickets.size() +
                ". Total sold: " + totalTicketsSold + "/" + maxCapacity, "INFO");

        notifyStatus();
        notifyAll();
        return true;
    }

    /**
     * Broadcasts the current status of the ticket pool.
     * Creates a TicketStatus object and,
     * sends the total tickets released, remaining tickets through websocket.
     */
    private void notifyStatus() {
        TicketStatus status = new TicketStatus();
        status.setRemainingTickets(allTicketsSold ? 0 : tickets.size());
        status.setTotalTicketsReleased(totalTicketsReleased);
        status.setAllTicketsReleased(allTicketsReleased);
        status.setAllTicketsSold(allTicketsSold);
        messagingTemplate.convertAndSend("/topic/status", status);
    }

    /**
     * Method to check if the ticket pool is complete.
     * @return (true if all tickets are sold, false if not)
     */
    public synchronized boolean isComplete() {
        return totalTicketsSold >= maxCapacity;
    }

    /**
     * Method to log the status of the ticket pool.
     * Creates a SystemLogger object and sends the log message through websocket.
     * @param message (log message)
     * @param level (log level)
     */
    private void logStatus(String message, String level) {
        SystemLogger log = new SystemLogger();
        log.setLogTime(LocalDateTime.now());
        log.setLevel(level);
        log.setMessage(message);
        messagingTemplate.convertAndSend("/topic/logs", log);
    }

    /**
     * Method to get the total tickets released.
     * @return (total tickets released)
     */
    public boolean areAllTicketsReleased() {
        return allTicketsReleased;
    }

    /**
     * Method to get the total tickets sold.
     * @return (total tickets sold)
     */
    public boolean areAllTicketsSold() {
        return allTicketsSold;
    }

    /**
     * Method to get the total tickets released.
     * @return (total tickets released)
     */
    public int getTotalTicketsReleased() {
        return totalTicketsReleased;
    }

    /**
     * Method to get the remaining tickets in the pool.
     * @return (remaining tickets)
     */
    public int getRemainingTickets() {
        return tickets.size();
    }
}
