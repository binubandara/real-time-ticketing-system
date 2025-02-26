import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WebSocketService } from '../../services/websocket.service';
import { TicketingService } from '../../services/ticketing.service';
import { ConfigurationInputComponent } from '../configuration-input/configuration-input.component';
import { SystemLogComponent } from '../system-log/system-log.component';
import { TicketStatusComponent } from '../ticket-status/ticket-status.component';
import { Subscription } from 'rxjs';
import { CustomerComponent } from '../customer/customer.component';
import { VendorComponent } from '../vendor/vendor.component';
import { TicketAnalyticsComponent } from '../ticket-analytics/ticket-analytics.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    ConfigurationInputComponent, 
    SystemLogComponent, 
    TicketStatusComponent, 
    CustomerComponent, 
    VendorComponent, 
    TicketAnalyticsComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {
  // Variable to check if simulation is running
  isRunning = false;

  // Subscription to listen to WebSocket status updates
  private statusSubscription?: Subscription;

  constructor(
    private websocketService: WebSocketService, // WebSocket service for real-time updates
    private ticketingService: TicketingService // Ticketing service for simulation control
  ) {}

  ngOnInit() {
    this.websocketService.connect();
    this.statusSubscription = this.websocketService.status$.subscribe(status => {
      this.isRunning = !status.allTicketsSold;
    });
  }

  ngOnDestroy() {
    this.statusSubscription?.unsubscribe();
    this.websocketService.disconnect();
  }

   // Start the ticket simulation
  startSimulation() {
    this.ticketingService.startSimulation().subscribe({
      next: () => this.isRunning = true, // Update `isRunning` to true
      error: (error) => console.error('Error starting simulation:', error)
    });
  }

  // Stop the ticket simulation
  stopSimulation() {
    this.ticketingService.stopSimulation().subscribe({
      next: () => this.isRunning = false,
      error: (error) => console.error('Error stopping simulation:', error)
    });
  }
}