import { Component, OnInit, OnDestroy, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, registerables } from 'chart.js';
import { WebSocketService } from '../../services/websocket.service';
import { Subscription } from 'rxjs';

// Register Chart.js modules
Chart.register(...registerables);

// Component to display a real-time update visual chart for sales and releases
@Component({
  selector: 'app-ticket-analytics',
  standalone: true,
  imports: [CommonModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './ticket-analytics.component.html',
  styleUrl: './ticket-analytics.component.css'
})
export class TicketAnalyticsComponent implements OnInit, OnDestroy {
  private chart: Chart | null = null;
  private logSubscription?: Subscription; // Subscription to WebSocket logs
  
  // Data for the chart
  private labels: string[] = []; // X-axis labels (time sequence)
  private releasedData: number[] = []; // Y-axis data for tickets released
  private purchasedData: number[] = []; // Y-axis data for tickets purchased

  constructor(private websocketService: WebSocketService) {}

  ngOnInit() {
    this.initializeChart(); // Set up the chart
    this.subscribeToLogs(); // Start listening to log updates
  }

  ngOnDestroy() {
    this.logSubscription?.unsubscribe();
    this.chart?.destroy();
  }

   // Initializes the Chart.js instance
   private initializeChart() {
    const ctx = document.querySelector('canvas') as HTMLCanvasElement; // Get canvas element

    this.chart = new Chart(ctx, {
      type: 'line', // Line chart
      data: {
        labels: this.labels, // X-axis labels
        datasets: [
          {
            label: 'Tickets Released', // Dataset label
            data: this.releasedData, // Data for tickets released
            borderColor: 'rgb(255, 99, 132)', 
            backgroundColor: 'rgb(255, 99, 132)', 
            tension: 0.1 
          },
          {
            label: 'Tickets Purchased', // Dataset label
            data: this.purchasedData, // Data for tickets purchased
            borderColor: 'rgb(54, 162, 235)', 
            backgroundColor: 'rgb(54, 162, 235)', 
            tension: 0.1 
          }
        ]
      },
      options: {
        responsive: true, 
        maintainAspectRatio: false, 
        scales: {
          y: {
            beginAtZero: true, // Start Y-axis at zero
            title: {
              display: true, // Show Y-axis title
              text: 'Number of Tickets' // Y-axis title text
            }
          },
          x: {
            title: {
              display: true, // Show X-axis title
              text: 'Time Sequence' // X-axis title text
            }
          }
        }
      }
    });
  }

  // Subscribes to WebSocket logs and processes them
  private subscribeToLogs() {
    this.logSubscription = this.websocketService.logs$.subscribe(log => {
      if (log.message && typeof log.message === 'string') {
        this.processLogMessage(log.message); // Parse the log message
      }
    });
  }

  // Extracts ticket-related data from log messages
  private processLogMessage(message: string) {
    const releaseMatch = message.match(/Vendor (V\d+) released (\d+) tickets/); // Match ticket release logs
    const purchaseMatch = message.match(/Customer (C\d+) purchased (\d+) tickets/); // Match ticket purchase logs

    if (releaseMatch) {
      this.updateChartData('released', parseInt(releaseMatch[2], 10)); // Update chart with release data
    } else if (purchaseMatch) {
      this.updateChartData('purchased', parseInt(purchaseMatch[2], 10)); // Update chart with purchase data
    }
  }

  // Updates chart data and redraws it
  private updateChartData(type: 'released' | 'purchased', value: number) {
    if (!this.chart) return; // Ensure the chart exists

    const timestamp = new Date().toLocaleTimeString(); // Get current time for the X-axis label

    this.labels.push(timestamp); // Add timestamp to labels

    if (type === 'released') {
      this.releasedData.push(value); // Add released data point
      this.purchasedData.push(0); // Add zero for purchased data
    } else {
      this.purchasedData.push(value); // Add purchased data point
      this.releasedData.push(0); // Add zero for released data
    }

    // Limit the chart to the last 20 data points
    if (this.labels.length > 20) {
      this.labels.shift(); // Remove the oldest label
      this.releasedData.shift(); // Remove the oldest released data point
      this.purchasedData.shift(); // Remove the oldest purchased data point
    }

    this.chart.update(); // Redraw the chart
  }
}