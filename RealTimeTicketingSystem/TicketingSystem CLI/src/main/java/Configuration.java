import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Manages configuration settings for the ticketing system.
 * Contains methods to get user input for configuration settings,
 * validate the input, save the configuration to a file,
 * load the configuration from a file, using Gson library.
 */
public class Configuration {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;
    private int noOfVendors;
    private int noOfCustomers;
    private int ticketsPerRelease;

    private static final Logger logger = SystemLogger.getLogger();

    private static final int MIN_RATE = 1;
    private static final int MAX_RATE = 10;

    public int getTotalTickets() {return totalTickets;}

    public int getTicketReleaseRate() {return ticketReleaseRate;}

    public int getCustomerRetrievalRate() {return customerRetrievalRate;}

    public int getMaxTicketCapacity() {return maxTicketCapacity;}

    public int getNoOfVendors() {return noOfVendors;}

    public int getNoOfCustomers() {return noOfCustomers;}

    public int getTicketsPerRelease() {return ticketsPerRelease;}


    /**
     * Method to get user input for configuration settings.
     * Calls validateConfiguration and
     * validateRate methods to validate the input.
     */
    public void userInput() {
        Scanner input = new Scanner (System.in);

        System.out.println("Enter the total number of vendors: ");
        this.noOfVendors = validateConfiguration(input);

        System.out.println("Enter the total number of customers: ");
        this.noOfCustomers = validateConfiguration(input);

        // Get total tickets
        System.out.println("Enter the total number of tickets available: ");
        this.totalTickets = validateConfiguration(input);

        // Get ticket release rate
        System.out.println("Enter the ticket release rate (per second): ");
        this.ticketReleaseRate = validateRate(input)*1000;

        // Get customer retrieval rate
        System.out.println("Enter the customer retrieval rate (per second): ");
        this.customerRetrievalRate = validateRate(input)*1000;

        //Get max ticket capacity
        System.out.println("Enter the maximum ticket capacity: ");
        this.maxTicketCapacity = validateConfiguration(input);

    }

    /**
     * Method to validate the configuration input.
     * @param input Scanner object for user input
     * @return Integer value of the validated configuration
     */
    public int validateConfiguration(Scanner input) {
        boolean isValid = false;
        int tickets = 0;

        while(!isValid){
            try {
                tickets = Integer.parseInt(input.nextLine());
                if(tickets <= 0) {
                    System.out.println("Please enter a positive number of tickets.");
                } else {
                    isValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                input.next();
            }
        } return tickets;
    }

    /**
     * Method to validate the rate input.
     * @param input Scanner object for user input
     * @return Integer value of the validated configuration rates
     */
    public int validateRate(Scanner input) {
        boolean isValid = false;
        int rate = 0;

        while(!isValid){
            try {
                rate = Integer.parseInt(input.nextLine());
                if(rate < MIN_RATE || rate > MAX_RATE) {
                    System.out.println("Please enter a rate between 1 and 10.");
                } else {
                    isValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                input.next();
            }
        } return rate;
    }

    /**
     * Saves the current configuration to a JSON file.
     * Uses Gson to create a pretty-printed JSON representation of the configuration.
     * Logs success or failure of the save operation.
     */
    public void saveConfigurationToFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("configuration.json")) {
            gson.toJson(this, writer);
            logger.info("Configuration saved to file.");
        } catch (IOException e) {
            logger.warning("An error occurred while saving the configuration." + e.getMessage());
        }
    }

    /**
     * Loads the configuration from a JSON file.
     * Uses Gson to parse the JSON file and create a Configuration object.
     * Logs success or failure of the load operation.
     * @return Configuration object loaded from the file
     */
    public static Configuration loadConfigurationFromFile() {
        Gson gson = new Gson();
        try(FileReader reader = new FileReader("configuration.json")) {
            logger.info("Configuration loaded from file.");
            return gson.fromJson(reader, Configuration.class);
        } catch (IOException e) {
            logger.warning("An error occurred while loading the configuration." + e.getMessage());
            return null;
        }
    }
}
