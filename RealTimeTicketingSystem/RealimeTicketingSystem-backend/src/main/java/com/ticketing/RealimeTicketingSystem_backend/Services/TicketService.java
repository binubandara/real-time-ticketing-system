package com.ticketing.RealimeTicketingSystem_backend.Services;

import com.ticketing.RealimeTicketingSystem_backend.Components.TicketPool;
import com.ticketing.RealimeTicketingSystem_backend.Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * Service class for managing ticket-related operations during the ticket simulation.
 * Provides methods to start and stop the simulation, and monitor the status of the tickets.
 */
@Service
public class TicketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final VendorService vendorService;
    private final CustomerService customerService;
    private final TicketPool ticketPool;

    /**
     * List to store all service threads currently running in the simulation.
     * This ensures that threads can be tracked and managed (stopped or restarted).
     */
    private final List<Thread> serviceThreads = Collections.synchronizedList(new ArrayList<>());

    /**
     * A volatile flag indicating if the simulation is currently running.
     * This is used to manage the simulation state across multiple threads safely.
     */
    public static volatile boolean isRunning = false;

    /**
     * Constructor for TicketService
     * @param messagingTemplate (Template for sending messages to the frontend)
     * @param vendorService (Service for managing vendor-related operations)
     * @param customerService (Service for managing customer-related operations)
     * @param ticketPool (Shared ticket pool for the simulation)
     */
    @Autowired
    public TicketService(SimpMessagingTemplate messagingTemplate,
                         VendorService vendorService,
                         CustomerService customerService,
                         TicketPool ticketPool) {
        this.messagingTemplate = messagingTemplate;
        this.vendorService = vendorService;
        this.customerService = customerService;
        this.ticketPool = ticketPool;
    }

    /**
     * Starts the ticket simulation with the given configuration.
     * Initializes the ticket pool with the given configuration and starts the vendor, customer and monitoring threads.
     * @param config (Configuration for the simulation entered by the user)
     */
    public synchronized void startSimulation(Configuration config) {
        if (isRunning) {
            throw new IllegalStateException("Simulation is already running");
        }

        // Log simulation start
        SystemLogger startLog = new SystemLogger();
        startLog.setLogTime(LocalDateTime.now());
        startLog.setLevel("INFO");
        startLog.setMessage("Starting simulation with configuration: " +
                "Initial Tickets=" + config.getTotalTickets() +
                ", Max Capacity=" + config.getMaxTicketCapacity() +
                ", Vendors=" + config.getNoOfVendors() +
                ", Customers=" + config.getNoOfCustomers());
        messagingTemplate.convertAndSend("/topic/logs", startLog);

        // Initialize ticket pool with configuration
        ticketPool.initialize(config);

        isRunning = true;

        // Start Vendor Threads
        vendorService.startVendors(config, ticketPool, messagingTemplate);

        // Start Customer Threads
        customerService.startCustomers(config, ticketPool, messagingTemplate);

        // Start a monitoring thread for tracking ticket status
        Thread monitorThread = new Thread(this::monitorTicketStatus);
        serviceThreads.add(monitorThread);
        monitorThread.start();

        // Log successful simulation start
        SystemLogger successLog = new SystemLogger();
        successLog.setLogTime(LocalDateTime.now());
        successLog.setLevel("INFO");
        successLog.setMessage("Simulation started successfully");
        messagingTemplate.convertAndSend("/topic/logs", successLog);
    }

    /**
     * Stops the ticket simulation.
     * Stops the vendor, customer and monitoring threads,
     * and logs the final status of the simulation.
     */
    public synchronized void stopSimulation() {
        if (!isRunning) {
            return;
        }

        // Log simulation stop
        SystemLogger stopLog = new SystemLogger();
        stopLog.setLogTime(LocalDateTime.now());
        stopLog.setLevel("INFO");
        stopLog.setMessage("Stopping simulation...");
        messagingTemplate.convertAndSend("/topic/logs", stopLog);

        isRunning = false;

        // Stop Vendor and Customer Threads
        vendorService.stopVendors();
        customerService.stopCustomers();

        // Stop the monitoring thread and any other service threads
        synchronized (serviceThreads) {
            for (Thread thread : serviceThreads) {
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
            }
            serviceThreads.clear();
        }

        // Log final simulation status
        SystemLogger finalLog = new SystemLogger();
        finalLog.setLogTime(LocalDateTime.now());
        finalLog.setLevel("INFO");
        finalLog.setMessage("Simulation stopped. Final stats: " +
                "Total tickets released=" + ticketPool.getTotalTicketsReleased() +
                ", Remaining tickets=" + ticketPool.getRemainingTickets() +
                ", All tickets released=" + ticketPool.areAllTicketsReleased() +
                ", All tickets sold=" + ticketPool.areAllTicketsSold());
        messagingTemplate.convertAndSend("/topic/logs", finalLog);
    }

    /**
     * Monitors the status of the tickets in the ticket pool.
     * Sends the status to the frontend at regular intervals.
     * Endpoint: "/topic/status"
     * Error handling: If the thread is interrupted, stop monitoring the status.
     */
    private void monitorTicketStatus() {
        while (isRunning) {
            synchronized (ticketPool) {
                TicketStatus status = new TicketStatus();
                //If all tickets are sold, set remaining tickets to 0 and all tickets sold to true
                if (ticketPool.isComplete()) {
                    status.setRemainingTickets(0);
                    status.setAllTicketsSold(true);
                    messagingTemplate.convertAndSend("/topic/status", status);
                    break; // Stop monitoring once the simulation is complete
                }
                //Otherwise, send the current status to the frontend
                else {
                    status.setRemainingTickets(ticketPool.getRemainingTickets());
                    status.setAllTicketsSold(ticketPool.areAllTicketsSold());
                    status.setTotalTicketsReleased(ticketPool.getTotalTicketsReleased());
                    status.setAllTicketsReleased(ticketPool.areAllTicketsReleased());
                    messagingTemplate.convertAndSend("/topic/status", status);
                }
            }

            try {
                Thread.sleep(1000); // Check status every second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}