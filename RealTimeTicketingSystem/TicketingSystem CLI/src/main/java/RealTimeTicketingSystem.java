import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RealTimeTicketingSystem {

    private static final Logger logger = SystemLogger.getLogger();
    public static boolean isRunning = false;
    private static List<Thread> vendorThreads = new ArrayList<>();
    private static List<Thread> customerThreads = new ArrayList<>();
    private static TicketPool ticketPool;
    private static Configuration userConfiguration;

    public static void main(String[] args) {

        System.out.println("Welcome to the Real Time Ticketing System");
        Data.connectToMongoDB();
        userConfiguration = Configuration.loadConfigurationFromFile();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            if (userConfiguration != null) {
                logger.info("User configuration loaded successfully.");
                displayConfiguration(userConfiguration);

                System.out.println("Do you want to continue with the above settings? (yes/no): ");
                String choice = reader.readLine().trim().toLowerCase();

                if (choice.equals("no")) {
                    logger.info("User chose to enter new configuration settings.");
                    userConfiguration = new Configuration();
                    userConfiguration.userInput();
                    userConfiguration.saveConfigurationToFile();
                } else {
                    logger.info("User chose to continue with existing configuration.");
                }
            } else {
                logger.warning("User configuration not found. Prompting user to enter configuration.");
                userConfiguration = new Configuration();
                userConfiguration.userInput();
                userConfiguration.saveConfigurationToFile();
            }

            ticketPool = new TicketPool(userConfiguration.getMaxTicketCapacity(), userConfiguration.getTotalTickets());
            logger.info("Initialized ticket pool with " + userConfiguration.getTotalTickets() + " tickets.");

            // Prompt for commands (start/stop)
            String command;
            System.out.println("\nEnter a command (start, stop): ");
            while ((command = reader.readLine().trim().toLowerCase()) != null) {
                switch (command) {
                    case "start":
                        if (!isRunning) {
                            startSimulation();
                        } else {
                            System.out.println("Simulation is already running.");
                        }
                        break;
                    case "stop":
                        stopSimulation();
                        return; // Exit after stopping
                    default:
                        System.out.println("Invalid command. Please enter 'start' or 'stop'.");
                }
            }
        } catch (Exception e) {
            logger.severe("Error reading input: " + e.getMessage());
        }
    }

    private static void displayConfiguration(Configuration config) {
        System.out.println("\nLoaded Configuration Details:");
        System.out.println("-----------------------------------");
        System.out.println("Total Tickets: " + config.getTotalTickets());
        System.out.println("Ticket Release Rate (ms): " + config.getTicketReleaseRate());
        System.out.println("Tickets per Release: " + config.getTicketsPerRelease());
        System.out.println("Customer Retrieval Rate (ms): " + config.getCustomerRetrievalRate());
        System.out.println("Max Ticket Capacity: " + config.getMaxTicketCapacity());
        System.out.println("Number of Vendors: " + config.getNoOfVendors());
        System.out.println("Number of Customers: " + config.getNoOfCustomers());
        System.out.println("-----------------------------------");
    }

    private static void startSimulation() {
        System.out.println("Starting the simulation...");
        isRunning = true;

        // Start Vendor Threads
        for (int i = 0; i < userConfiguration.getNoOfVendors(); i++) {
            Vendor vendor = new Vendor("V" + (i + 1), userConfiguration.getTicketsPerRelease(), ticketPool);
            Thread vendorThread = new Thread(vendor);
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        // Start Customer Threads
        for (int i = 0; i < userConfiguration.getNoOfCustomers(); i++) {
            Customer customer = new Customer("C" + (i + 1), userConfiguration.getCustomerRetrievalRate(), ticketPool);
            Thread customerThread = new Thread(customer);
            customerThreads.add(customerThread);
            customerThread.start();
        }

        logger.info("Simulation started with " + vendorThreads.size() + " vendors and " + customerThreads.size() + " customers.");
    }

    private static void stopSimulation() {
        System.out.println("Stopping the simulation...");
        isRunning = false;

        // Gracefully stop Vendor Threads
        for (Thread vendorThread : vendorThreads) {
            try {
                vendorThread.interrupt();  // Interrupt the thread
                vendorThread.join();  // Wait for the thread to finish its task
            } catch (InterruptedException e) {
                logger.warning("Vendor thread interrupted: " + e.getMessage());
            }
        }

        // Gracefully stop Customer Threads
        for (Thread customerThread : customerThreads) {
            try {
                customerThread.interrupt();  // Interrupt the thread
                customerThread.join();  // Wait for the thread to finish its task
            } catch (InterruptedException e) {
                logger.warning("Customer thread interrupted: " + e.getMessage());
            }
        }

        vendorThreads.clear();
        customerThreads.clear();
        logger.warning("Simulation stopped.");
    }
}
