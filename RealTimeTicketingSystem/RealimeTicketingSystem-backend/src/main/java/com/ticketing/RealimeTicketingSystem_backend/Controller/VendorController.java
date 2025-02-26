package com.ticketing.RealimeTicketingSystem_backend.Controller;

import com.ticketing.RealimeTicketingSystem_backend.Models.Vendor;
import com.ticketing.RealimeTicketingSystem_backend.Repo.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for vendor
 * Provides endpoints for getting all vendors
 */
@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "http://localhost:4200")
public class VendorController {

    private final VendorRepository vendorRepository;

    /**
     * Constructor for vendor controller
     * @param vendorRepository (vendor repository for database operations)
     */
    @Autowired
    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    /**
     * Get all vendors
     * @return list of all vendors
     */
    @GetMapping
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }
}