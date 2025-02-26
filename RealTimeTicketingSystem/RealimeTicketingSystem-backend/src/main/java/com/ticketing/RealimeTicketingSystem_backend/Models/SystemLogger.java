package com.ticketing.RealimeTicketingSystem_backend.Models;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * SystemLogger class is used to store the logs of the system
 */
@Data
public class SystemLogger {
    private LocalDateTime logTime; // Time of the log
    private String level; // Level of the log (INFO, ERROR, WARN)
    private String message; // Message of the log

    /**
     * Default constructor for SystemLogger.
     * Initializes an empty log entry.
     */
    public SystemLogger() {
        // Default constructor
    }
}
