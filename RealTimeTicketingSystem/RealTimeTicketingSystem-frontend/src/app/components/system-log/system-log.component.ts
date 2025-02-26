import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WebSocketService } from '../../services/websocket.service';
import { SystemLog } from '../../models/systemLog';

//Component to display system logs in real-time
@Component({
  selector: 'app-system-log',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './system-log.component.html',
  styleUrl: './system-log.component.css'
})
export class SystemLogComponent {
  logs: SystemLog[] = []; // Array to store system logs

  constructor(private websocketService: WebSocketService) {
    // Subscribing to WebSocket service to listen for new logs
    this.websocketService.logs$.subscribe(log => {
      this.logs.unshift(log); // Add new log to the start of the list
      if (this.logs.length > 100) {
        this.logs.pop(); // Remove oldest log if the array exceeds 100 logs
      }
    });
  }
}