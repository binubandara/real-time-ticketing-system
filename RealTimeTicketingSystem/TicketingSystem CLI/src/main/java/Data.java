import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Data class to handle MongoDB operations.
 * Responsible for connecting to the MongoDB database and
 * saving customer and vendor details.
 */
public class Data {
    private static final Logger logger = SystemLogger.getLogger();
    private static MongoDatabase database;
    private static String customerCollection;
    private static String vendorCollection;

    /**
     * Connect to the MongoDB database.
     * If the connection is successful, create the required collections.
     * If the connection fails, log the error message.
     */
    public static void connectToMongoDB() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("RealTimeTicketingSystem");
            logger.info("Connected to database successfully");

            createSimulationCollections();


        } catch (Exception e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        }
    }

    /**
     * Create collections for storing customer and vendor details.
     * The collection names are generated based on the current timestamp.
     *
     */
    public static void createSimulationCollections() {
        String  timestamp = getCurrentTimestamp();
        customerCollection = "simulation_" + timestamp + "_customers";
        vendorCollection = "simulation_" + timestamp + "_vendors";
    }

    /**
     * Get the current timestamp in the format "yyyy-MM-dd-HH-mm-ss".
     * @return Current timestamp
     */
    private static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return sdf.format(new Date());
    }

    /**
     * Save the customer purchase details to the MongoDB database.
     * @param customerId Unique identifier for the customer
     * @param totalTicketsBought Total number of tickets purchased by the customer
     */
    public static void saveCustomerDetails(String customerId, int totalTicketsBought) {

        if (customerCollection == null) {
            logger.severe("Customer collection not initialized!");
            throw new IllegalStateException("Customer collection is not initialized.");
        }

        MongoCollection<Document> collection = database.getCollection(customerCollection);

        collection.updateOne(
                Filters.eq("customerId", customerId),
                Updates.inc("totalTicketsBought", totalTicketsBought),
                new com.mongodb.client.model.UpdateOptions().upsert(true)
        );

    }

    /**
     * Save the vendor release details to the MongoDB database.
     * @param vendorId Unique identifier for the vendor
     * @param ticketsReleasedInBatch Total number of tickets released by the vendor
     */
    public static void saveVendorDetails(String vendorId, int ticketsReleasedInBatch) {
        if (vendorCollection == null) {
            logger.severe("Vendor collection not initialized!");
            throw new IllegalStateException("Vendor collection is not initialized.");
        }

        MongoCollection<Document> collection = database.getCollection(vendorCollection);

        // Find the current document
        Document vendor = collection.find(Filters.eq("vendorId", vendorId)).first();

        if (vendor == null) {
            // If no document exists, create new one with initial batch
            Document newVendorDoc = new Document()
                    .append("vendorId", vendorId)
                    .append("totalTicketsReleased", ticketsReleasedInBatch)
                    .append("lastUpdate", new Date());

            collection.insertOne(newVendorDoc);
        } else {
            // Update existing document with new batch
            collection.updateOne(
                    Filters.eq("vendorId", vendorId),
                    Updates.combine(
                            Updates.inc("totalTicketsReleased", ticketsReleasedInBatch),
                            Updates.set("lastUpdate", new Date())
                    )
            );
        }
    }
}


