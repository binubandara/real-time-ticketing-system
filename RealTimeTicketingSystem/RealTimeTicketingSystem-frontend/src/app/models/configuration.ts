// Interface defining the configuration properties for the ticketing system
export interface Configuration {
  id?: string; // Optional unique identifier
  totalTickets: number; // Total number of tickets already available in the system
  ticketReleaseRate: number; // Rate at which vendors release tickets
  customerRetrievalRate: number; // Rate at which customers purchase tickets
  maxTicketCapacity: number; // Maximum capacity of tickets
  noOfVendors: number; // Number of vendors in the system
  noOfCustomers: number; // Number of customers in the system
}
