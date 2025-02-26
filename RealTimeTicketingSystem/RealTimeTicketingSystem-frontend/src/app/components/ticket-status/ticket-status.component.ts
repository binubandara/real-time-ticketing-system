import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WebSocketService } from '../../services/websocket.service';
import { TicketStatus } from '../../models/ticketStatus';

// Component to manage real-time ticket status display
@Component({
  selector: 'app-ticket-status',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ticket-status.component.html',
  styleUrl: './ticket-status.component.css'
})
export class TicketStatusComponent {
  status?: TicketStatus; // Holds the current ticket status received from the server.

   // Subscribes to ticket status updates from the WebSocket service.
  constructor(private websocketService: WebSocketService) {
    this.websocketService.status$.subscribe(status => {
      this.status = status;
    });
  }

  // Returns the number of remaining tickets.
  getRemainingTickets(): number {
    if (this.status?.allTicketsSold) return 0; 
    return this.status?.remainingTickets ?? 0; //Set default amount to 0
  }

  // Determines the status message based on the current ticket system state.
  getStatusMessage(): string {
    if (!this.status) return 'System not initialized'; // Message if the system hasn't started.
    if (this.status.allTicketsSold) return 'All tickets sold'; // Message if all tickets are sold.
    if (this.status.allTicketsReleased) return 'All tickets released'; // Message if all tickets are released.
    return 'System active';
  }

  getStatusAlertClass(): string {
    if (!this.status) return 'alert-secondary';
    if (this.status.allTicketsSold) return 'alert-success'; // Green alert if tickets are sold.
    if (this.status.allTicketsReleased) return 'alert-warning'; // Yellow alert if tickets are released.
    return 'alert-info';
  }
}