import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Customer } from '../../models/customer';
import { HttpClient } from '@angular/common/http';
import { WebSocketService } from '../../services/websocket.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css'
})
export class CustomerComponent implements OnInit, OnDestroy {
  customers: Customer[] = []; // Array to store customer statistics
  private customerSubscription: Subscription | null = null; // Subscribe to websocket logs

  constructor(
    private http: HttpClient, // To fetch customer data using API calls
    private webSocketService: WebSocketService 
  ) {}

  ngOnInit() {
    // Initial load
    this.loadCustomerStats();

    // Subscribe to WebSocket logs to update customer statistics in real-time
    this.customerSubscription = this.webSocketService.logs$.subscribe(log => {
      if (log.message && log.message.includes('Customer')) {
        // Reload customer stats on relevant logs
        this.loadCustomerStats();
      }
    });
  }

  ngOnDestroy() {
    if (this.customerSubscription) {
      this.customerSubscription.unsubscribe();
    }
  }


  // Fetches customer statistics from the backend
  loadCustomerStats() {
    this.http.get<Customer[]>('http://localhost:8080/api/customers').subscribe({
      next: (data) => {
        // Sort customers by ticket purchases
        this.customers = data.sort((a, b) => b.totalTicketsPurchased - a.totalTicketsPurchased);
      },
      error: (error) => console.error('Error loading customer stats:', error)
    });
  }
}