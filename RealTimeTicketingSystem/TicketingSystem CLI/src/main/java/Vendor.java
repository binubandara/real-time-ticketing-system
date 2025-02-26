import java.util.Random;
import java.util.logging.Logger;

/**
 * Vendor class to simulate a vendor releasing tickets at regular intervals.
 * Implements Runnable interface to run as a separate thread.
 */
class Vendor implements Runnable {
    private final String vendorId;
    private final int releaseInterval;
    private final TicketPool ticketPool;
    private final Random random = new Random();
    private final Logger logger = SystemLogger.getLogger();

    private int batchTicketsReleased = 0;

    /**
     * Constructor for Vendor
     * @param vendorId (ID of the vendor)
     * @param releaseInterval (Interval at which tickets are released)
     * @param ticketPool (Shared ticket pool for the simulation)
     */
    public Vendor(String vendorId, int releaseInterval, TicketPool ticketPool) {
        this.vendorId = vendorId;
        this.releaseInterval = releaseInterval;
        this.ticketPool = ticketPool;
    }

    /**
     * Run method for the vendor task.
     * Releases tickets at the given interval until all tickets are released
     * or the thread is interrupted.
     * Error handling is done for saving vendor release details.
     */
    @Override
    public void run() {
        while (RealTimeTicketingSystem.isRunning) {
            // Check if max capacity reached before trying to add tickets
            if (Thread.interrupted() || ticketPool.areAllTicketsReleased()) {
                logger.info("Vendor " + vendorId + " stopping - max capacity reached.");
                break; // Exit the thread if max capacity reached
            }

            //Releases 1-10 tickets per iteration
            int ticketsPerRelease = random.nextInt(10) + 1;

            synchronized (ticketPool) {
                if (ticketPool.areAllTicketsReleased()) {
                    break; // Double-check inside synchronized block
                }

                batchTicketsReleased = ticketsPerRelease;
                boolean added = ticketPool.addTickets(vendorId, batchTicketsReleased);

                if (!added) {
                    // If tickets couldn't be added due to capacity, exit thread
                    break;
                }

                try {
                    Data.saveVendorDetails(vendorId, batchTicketsReleased);
                } catch (Exception e) {
                    logger.warning("Saving details of vendor " + vendorId + " stopped: " + e.getMessage());
                }
            }

            try {
                Thread.sleep(releaseInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        logger.info("Vendor " + vendorId + " has stopped.");
    }
}
