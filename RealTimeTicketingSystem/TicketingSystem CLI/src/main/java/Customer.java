import java.util.Random;
import java.util.logging.*;

/**
 * Customer class to simulate a customer buying tickets.
 * Each customer thread will purchase a random number of tickets at the given interval.
 * Implements Runnable interface to run the customer thread.
 */
class Customer implements Runnable {
    private final String customerId;
    private final int retrievalRate;
    private final TicketPool ticketPool;
    private final Random random = new Random();
    private final Logger logger = SystemLogger.getLogger();

    /**
     * Constructor to initialize the customer details.
     * @param customerId (Unique identifier for the customer)
     * @param retrievalRate (Interval at which the customer will purchase tickets)
     * @param ticketPool (Shared ticket pool for the simulation)
     */
    public Customer(String customerId, int retrievalRate, TicketPool ticketPool) {
        this.customerId = customerId;
        this.retrievalRate = retrievalRate;
        this.ticketPool = ticketPool;
    }

    /**
     * Simulates customer ticket purchasing behavior.
     * Continues running while the system is active:
     * Attempts to buy 1-5 tickets per iteration
     * Saves customer purchase details
     * Stops if interrupted or all tickets are sold
     */
    @Override
    public void run() {
        while (RealTimeTicketingSystem.isRunning) {
            if (Thread.interrupted() || ticketPool.areAllTicketsSold()) {
                break;
            }

            int ticketsToBuy = random.nextInt(5) + 1;

            boolean purchased = ticketPool.removeTickets(customerId, ticketsToBuy);
            if (!purchased) {
                break;
            }

            try {
                Data.saveCustomerDetails(customerId, ticketsToBuy);
            } catch (Exception e) {
                logger.warning("Error saving customer details: " + e.getMessage());
            }

            try {
                Thread.sleep(retrievalRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("Customer " + customerId + " has stopped.");
    }
}
