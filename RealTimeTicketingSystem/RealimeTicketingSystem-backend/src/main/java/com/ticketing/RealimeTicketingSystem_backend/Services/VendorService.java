package com.ticketing.RealimeTicketingSystem_backend.Services;

import com.ticketing.RealimeTicketingSystem_backend.Components.TicketPool;
import com.ticketing.RealimeTicketingSystem_backend.Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.ticketing.RealimeTicketingSystem_backend.Repo.VendorRepository;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for managing vendor-related operations during the ticket simulation.
 * Provides methods to start and stop the vendors, and monitor the status of the vendors.
 */
@Service
public class VendorService {

    private final Random random = new Random();
    private final VendorRepository vendorRepository;
    private final List<Thread> vendorThreads = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructor for VendorService
     * @param vendorRepository (Repository for database operations)
     */
    @Autowired
    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    /**
     * Starts vendor threads based on the configuration provided when the simulation is started.
     * Each vendor thread will release tickets at the given interval.
     * @param config (Configuration for the simulation entered by the user)
     * @param ticketPool (Shared ticket pool for the simulation)
     * @param messagingTemplate (Template for sending messages to the frontend)
     */
    public void startVendors(Configuration config, TicketPool ticketPool, SimpMessagingTemplate messagingTemplate) {
        for (int i = 0; i < config.getNoOfVendors(); i++) {
            String vendorId = "V" + (i + 1);
            VendorTask task = new VendorTask(vendorId, config.getTicketReleaseRate(), ticketPool, messagingTemplate);
            Thread thread = new Thread(task);
            vendorThreads.add(thread);
            thread.start();
        }
    }

    /**
     * Stop vendor threads when the simulation is stopped.
     * Interrupts all vendor threads and clears the list of threads.
     */
    public void stopVendors() {
        synchronized (vendorThreads) {
            for (Thread thread : vendorThreads) {
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
            }
            vendorThreads.clear();
        }
    }

    /**
     * Inner class to represent a vendor task.
     * Each vendor task will release tickets at a given interval.
     */
    private class VendorTask implements Runnable {
        private final String vendorId;
        private final int releaseInterval;
        private final TicketPool ticketPool;
        private final SimpMessagingTemplate messagingTemplate;

        /**
         * Constructor for VendorTask
         * @param vendorId (ID of the vendor)
         * @param releaseInterval (Interval at which tickets are released)
         * @param ticketPool (Shared ticket pool for the simulation)
         * @param messagingTemplate (Template for sending messages to the frontend)
         */
        VendorTask(String vendorId, int releaseInterval, TicketPool ticketPool, SimpMessagingTemplate messagingTemplate) {
            this.vendorId = vendorId;
            this.releaseInterval = releaseInterval;
            this.ticketPool = ticketPool;
            this.messagingTemplate = messagingTemplate;
        }

        /**
         * Run method for the vendor task.
         * Releases tickets at the given interval until all tickets are released
         * or the thread is interrupted.
         * Error handling is done for saving vendor release details.
         */
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (ticketPool.areAllTicketsReleased()) {
                    break;
                }

                int ticketsToRelease = random.nextInt(20) + 1; // Release a random number of tickets between 1 and 20
                synchronized (ticketPool) {
                    boolean released = ticketPool.addTickets(vendorId, ticketsToRelease);

                    if (released) {
                        saveVendorRelease(vendorId, ticketsToRelease);
                    }
                }

                try {
                    Thread.sleep(releaseInterval*1000); // Sleep for the given interval
                                                             // converting seconds to milliseconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        /**
         * Save the details of the vendor release in the database.
         * If the vendor already exists, update the total tickets released.
         * If the vendor is new, create a new entry with the total tickets released.
         * @param vendorId (ID of the vendor)
         * @param ticketsToRelease (Number of tickets released by the vendor)
         */
        private synchronized void saveVendorRelease(String vendorId, int ticketsToRelease) {
            Optional<Vendor> existingVendor = vendorRepository.findById(vendorId);

            if (existingVendor.isPresent()) {
                Vendor release = existingVendor.get();
                release.setTotalTicketsReleased(release.getTotalTicketsReleased() + ticketsToRelease);
                vendorRepository.save(release);

                // Log for existing vendor
                SystemLogger log = new SystemLogger();
                log.setLogTime(LocalDateTime.now());
                log.setLevel("INFO");
                log.setMessage("Vendor " + vendorId + " released " + ticketsToRelease + " tickets. Total: " + release.getTotalTicketsReleased());
                messagingTemplate.convertAndSend("/topic/logs", log);
            } else {
                Vendor newRelease = new Vendor();
                newRelease.setVendorId(vendorId);
                newRelease.setTotalTicketsReleased(ticketsToRelease);
                vendorRepository.save(newRelease);

                // Log for new vendor
                SystemLogger log = new SystemLogger();
                log.setLogTime(LocalDateTime.now());
                log.setLevel("INFO");
                log.setMessage("New Vendor " + vendorId + " released " + ticketsToRelease + " tickets");
                messagingTemplate.convertAndSend("/topic/logs", log);
            }
        }
    }
}