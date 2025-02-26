import java.io.IOException;
import java.util.logging.*;

/**
 * A custom logging utility for the Real-Time Ticketing System.
 * The logger uses ANSI color codes to differentiate log levels:
 * - RED: Severe errors
 * - YELLOW: Warnings
 * - WHITE: General information
 * - BLUE: Fine/debug level messages
 */
public class SystemLogger {
    private static final Logger logger = Logger.getLogger("TicketingSystemLogger");

    // ANSI color codes
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_WHITE = "\u001B[37m";

    static {
        try {
            if (logger.getHandlers().length == 0) {
                // Custom Console Handler that uses System.out
                ConsoleHandler consoleHandler = new ConsoleHandler() {
                    @Override
                    protected synchronized void setOutputStream(java.io.OutputStream out) throws SecurityException {
                        super.setOutputStream(System.out);
                    }
                };

                // Custom Formatter with colors
                Formatter colorFormatter = new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        String color = ANSI_WHITE; // default color

                        // Set color based on log level
                        if (record.getLevel() == Level.SEVERE) {
                            color = ANSI_RED;
                        } else if (record.getLevel() == Level.WARNING) {
                            color = ANSI_YELLOW;
                        } else if (record.getLevel() == Level.INFO) {
                            color = ANSI_WHITE;
                        } else if (record.getLevel() == Level.FINE ||
                                record.getLevel() == Level.FINER ||
                                record.getLevel() == Level.FINEST) {
                            color = ANSI_BLUE;
                        }

                        return color + record.getLevel() + ": " + record.getMessage() + ANSI_RESET + "\n";
                    }
                };

                // Plain formatter for file output (no colors in file)
                Formatter fileFormatter = new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        return record.getLevel() + ": " + record.getMessage() + "\n";
                    }
                };

                // Configure console handler
                consoleHandler.setLevel(Level.ALL);
                consoleHandler.setFormatter(colorFormatter);
                logger.addHandler(consoleHandler);

                // File Handler setup (without colors)
                FileHandler fileHandler = new FileHandler("ticketingSystem.log");
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(fileFormatter);
                logger.addHandler(fileHandler);

                // Configure logger settings
                logger.setLevel(Level.ALL);
                logger.setUseParentHandlers(false);
            }
        } catch (IOException e) {
            System.out.println("Error occurred while creating logger: " + e.getMessage());
        }
    }


    public static Logger getLogger() {
        return logger;
    }

    // Convenience methods
    public static void info(String message) {
        logger.info(message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void severe(String message) {
        logger.severe(message);
    }

    public static void debug(String message) {
        logger.fine(message);
    }
}