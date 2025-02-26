export interface TicketStatus {
    remainingTickets: number; // Number of remaining tickets that aren't sold yet
    totalTicketsReleased: number; // Total number of tickets released by vendors
    allTicketsReleased: boolean; // Indicating if all tickets have been released
    allTicketsSold: boolean; // Indicating if all tickets have been sold.
  }
  