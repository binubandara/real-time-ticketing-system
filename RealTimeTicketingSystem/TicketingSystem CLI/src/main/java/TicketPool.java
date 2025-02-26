import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;


/**
 * TicketPool class to manage the tickets in the system.
 * Tread-safe methods to add and remove tickets,
 * and keep track of the total tickets released and sold.
 */
public class TicketPool {
    private final List<Integer> tickets = Collections.synchronizedList(new ArrayList<>());
    private final int maxCapacity;
    private int totalTicketsReleased = 0;
    private static final Logger logger = SystemLogger.getLogger();
    private boolean allTicketsReleased = false;
    private boolean allTicketsSold = false;  // New flag to track if all tickets are sold

    /**
     * Constructor for TicketPool
     * Initializes the ticket pool with the given maximum capacity and initial tickets.
     * @param maxCapacity (Maximum capacity of the ticket pool)
     * @param initialTickets (Initial number of tickets in the pool)
     */
    public TicketPool(int maxCapacity, int initialTickets) {
        this.maxCapacity = maxCapacity;
        for (int i = 0; i < initialTickets && i < maxCapacity; i++) {
            tickets.add(1);
            totalTicketsReleased++;
        }
        logger.info("Initialized ticket pool with " + tickets.size() + " tickets. Maximum capacity: " + maxCapacity);
    }

    /**
     * Method to add tickets to the pool from a vendor.
     * Checks if the pool has reached the maximum capacity,
     * and adds the specified number of tickets to the pool.
     * @param vendorId (ID of the vendor adding tickets)
     * @param numTickets (Number of tickets to add)
     * @return true if tickets are successfully added, false otherwise
     */
    public synchronized boolean addTickets(String vendorId, int numTickets) {
        if (allTicketsReleased || allTicketsSold) {
            return false;
        }

        // Check if the pool has reached the maximum capacity
        int remainingCapacity = maxCapacity - totalTicketsReleased;
        if (remainingCapacity <= 0) {
            allTicketsReleased = true;
            logger.info("Maximum ticket capacity reached. No more tickets can be released.");
            return false;
        }

        // Add tickets up to the remaining capacity or the requested number of tickets
        int ticketsToAdd = Math.min(numTickets, remainingCapacity);

        for (int i = 0; i < ticketsToAdd; i++) {
            tickets.add(1);
        }
        totalTicketsReleased += ticketsToAdd;

        if (totalTicketsReleased >= maxCapacity) {
            allTicketsReleased = true;
            logger.info("All " + maxCapacity + " tickets have been released. Vendors will stop releasing tickets.");
        }

        logger.info("Vendor " + vendorId + " added " + ticketsToAdd + " tickets. Pool size: " + tickets.size() +
                ". Total released: " + totalTicketsReleased + "/" + maxCapacity);
        notifyAll();
        return true;
    }

    /**
     * Method to remove tickets from the pool for a customer.
     * Checks if all tickets are sold, and returns false.
     * Checks if there are enough tickets and all have been released,
     * and returns false.
     * @param customerId (ID of the customer purchasing tickets)
     * @param numTickets (Number of tickets to remove)
     * @return true if tickets are successfully removed, false otherwise
     */
    public synchronized boolean removeTickets(String customerId, int numTickets) {
        // If all tickets are already sold, return immediately
        if (allTicketsSold) {
            return false;
        }

        // If there aren't enough tickets and all have been released, return false
        if (tickets.size() < numTickets && allTicketsReleased) {
            return false;
        }

        // Wait for tickets if needed
        while (tickets.size() < numTickets && !allTicketsSold) {
            try {
                logger.warning("Pool has fewer than " + numTickets + " tickets. Customer " + customerId + " is waiting.");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.severe("Customer " + customerId + " interrupted.");
                return false;
            }
        }

        // Double check if we can proceed after waiting
        if (allTicketsSold || tickets.size() < numTickets) {
            return false;
        }

        for (int i = 0; i < numTickets; i++) {
            tickets.remove(tickets.size() - 1);
        }

        logger.info("Customer " + customerId + " purchased " + numTickets + " tickets. Pool size: " + tickets.size() +
                ". Total sold: " + (totalTicketsReleased - tickets.size()) + "/" + maxCapacity);

        // Check if all tickets are sold
        if (allTicketsReleased && tickets.isEmpty() && !allTicketsSold) {
            allTicketsSold = true;
            logger.info("All tickets have been sold. Awaiting stop command from user.");
            // Set isRunning to false to stop new transactions, but system will continue running
            RealTimeTicketingSystem.isRunning = false;
        }

        notifyAll();
        return true;
    }

    /**
     * Method to check if all tickets have been released.
     * @return true if all tickets have been released, false otherwise
     */
    public boolean areAllTicketsReleased() {
        return allTicketsReleased;
    }

    /**
     * Method to check if all tickets have been sold.
     * @return true if all tickets have been sold, false otherwise
     */
    public boolean areAllTicketsSold() {
        return allTicketsSold;
    }

    /**
     * Method to get the total number of tickets released so far.
     * @return total number of tickets released
     */
    public int getTotalTicketsReleased() {
        return totalTicketsReleased;
    }

    /**
     * Method to get the remaining number of tickets in the pool.
     * @return remaining number of tickets
     */
    public int getRemainingTickets() {
        return tickets.size();
    }
}