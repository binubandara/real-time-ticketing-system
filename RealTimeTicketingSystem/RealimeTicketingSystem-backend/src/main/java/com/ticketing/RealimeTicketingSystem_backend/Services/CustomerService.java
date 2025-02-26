package com.ticketing.RealimeTicketingSystem_backend.Services;

import com.ticketing.RealimeTicketingSystem_backend.Components.TicketPool;
import com.ticketing.RealimeTicketingSystem_backend.Models.*;
import com.ticketing.RealimeTicketingSystem_backend.Repo.CustomerRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for managing customer-related operations during the ticket simulation.
 */
@Service
public class CustomerService {

    private final Random random = new Random();
    private final CustomerRepository customerRepository;
    private final List<Thread> customerThreads = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructor for CustomerService
     * @param customerRepository (Repository for database operations)
     */
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Starts customer threads based on the configuration provided when the simulation is started.
     * Each custer thread will purchase tickets at the given interval.
     * @param config (Configuration for the simulation entered by the user)
     * @param ticketPool (Shared ticket pool for the simulation)
     * @param messagingTemplate (Template for sending messages to the frontend)
     */
    public void startCustomers(Configuration config, TicketPool ticketPool, SimpMessagingTemplate messagingTemplate) {
        for (int i = 0; i < config.getNoOfCustomers(); i++) {
            String customerId = "C" + (i + 1);
            CustomerTask task = new CustomerTask(customerId, config.getCustomerRetrievalRate(), ticketPool, messagingTemplate);
            Thread thread = new Thread(task);
            customerThreads.add(thread);
            thread.start();
        }
    }

    /**
     * Stop customer threads when the simulation is stopped.
     * Interrupts all customer threads and clears the list of threads.
     */
    public void stopCustomers() {
        synchronized (customerThreads) {
            for (Thread thread : customerThreads) {
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                }
            }
            customerThreads.clear();
        }
    }

    /**
     * Inner class for customer tasks.
     * Each customer thread will purchase tickets at the given interval.
     */
    private class CustomerTask implements Runnable {
        private final String customerId;
        private final int purchaseInterval;
        private final TicketPool ticketPool;
        private final SimpMessagingTemplate messagingTemplate;

        /**
         * Constructor for CustomerTask
         * @param customerId (Unique identifier for the customer)
         * @param purchaseInterval (Interval at which the customer will purchase tickets)
         * @param ticketPool (Shared ticket pool for the simulation)
         * @param messagingTemplate (Template for sending messages to the frontend)
         */
        CustomerTask(String customerId, int purchaseInterval, TicketPool ticketPool, SimpMessagingTemplate messagingTemplate) {
            this.customerId = customerId;
            this.purchaseInterval = purchaseInterval;
            this.ticketPool = ticketPool;
            this.messagingTemplate = messagingTemplate;
        }

        /**
         * Run method for the customer task.
         * Purchases tickets at the given interval and saves the customer details in the database.
         * Error handling is done for any exceptions that occur during the process.
         */
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                int ticketsToPurchase = random.nextInt(5) + 1; //Purchasing a random number of tickets between 1 and 5
                synchronized (ticketPool) {
                    boolean purchased = ticketPool.removeTickets(customerId, ticketsToPurchase);

                    if (purchased) {
                        saveCustomerDetails(customerId, ticketsToPurchase);

                    }
                }

                try {
                    Thread.sleep(purchaseInterval*1000); //Sleeping for the given interval
                                                              //converting the seconds to milliseconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        /**
         * Method to save the customer details in the database.
         * If the customer already exists, the total tickets purchased is updated.
         * If the customer is new, a new entry is created in the database.
         * @param customerId (Unique identifier for the customer)
         * @param ticketsPurchased (Number of tickets purchased by the customer)
         */
        private synchronized void saveCustomerDetails(String customerId, int ticketsPurchased) {
            Optional<Customer> existingCustomer = customerRepository.findByCustomerId(customerId);
            if (existingCustomer.isPresent()) {
                Customer customer = existingCustomer.get();
                customer.setTotalTicketsPurchased(customer.getTotalTicketsPurchased() + ticketsPurchased);
                customerRepository.save(customer);
            } else {
                Customer newCustomer = new Customer(customerId, ticketsPurchased);
                customerRepository.save(newCustomer);

            }
        }
    }
}