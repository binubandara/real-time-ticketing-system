// Vendor statistics component logic
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Vendor } from '../../models/vendor';
import { HttpClient } from '@angular/common/http';
import { WebSocketService } from '../../services/websocket.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-vendor',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vendor.component.html',
  styleUrl: './vendor.component.css'
})
export class VendorComponent implements OnInit, OnDestroy {
  vendors: Vendor[] = []; // Array to store vendor statistics
  private vendorSubscription: Subscription | null = null; // Subscription for WebSocket logs

  constructor(
    private http: HttpClient, // HTTP client for API calls
    private webSocketService: WebSocketService // WebSocket service for real-time updates
  ) {}

  ngOnInit() {
    // Load vendor stats on component initialization
    this.loadVendorStats();

    // Subscribe to WebSocket logs to track vendor updates
    this.vendorSubscription = this.webSocketService.logs$.subscribe(log => {
      // Check if the log message is related to vendor activity
      if (log.message && log.message.includes('Vendor')) {
        // Reload vendor stats when relevant logs are received
        this.loadVendorStats();
      }
    });
  }

  ngOnDestroy() {
    // Unsubscribe from WebSocket updates to avoid memory leaks
    if (this.vendorSubscription) {
      this.vendorSubscription.unsubscribe();
    }
  }

  loadVendorStats() {
    // Fetch vendor statistics from the backend API
    this.http.get<Vendor[]>('http://localhost:8080/api/vendors').subscribe({
      next: (data) => {
        // Sort vendors by total tickets released in descending order
        this.vendors = data.sort((a, b) => b.totalTicketsReleased - a.totalTicketsReleased);
      },
      error: (error) => console.error('Error loading vendor stats:', error)
    });
  }
}
