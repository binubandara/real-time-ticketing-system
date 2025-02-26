package com.ticketing.RealimeTicketingSystem_backend.Controller;

import com.ticketing.RealimeTicketingSystem_backend.Models.Customer;
import com.ticketing.RealimeTicketingSystem_backend.Repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for customer related operations
 * Provides an endpoint to get all customers
 */
@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    private final CustomerRepository customerRepository;

    /**
     * Constructor for CustomerController
     * @param customerRepository (Repository for database operations)
     */
    @Autowired
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Get all customers
     * @return List of all customers
     */
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}