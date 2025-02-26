import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Configuration } from "../models/configuration";

@Injectable({
  providedIn: 'root'
})

export class TicketingService {
    private apiUrl = 'http://localhost:8080/api'; //URL for SpringBoot API

    constructor(private http: HttpClient) {}

    // Save user entered configurations
    saveConfiguration(config: Configuration): Observable<Configuration> {
        return this.http.post<Configuration>(`${this.apiUrl}/configuration`, config);
    }
  
    // Fetch the latest configuration from backend
    getConfiguration(): Observable<Configuration> {
    return this.http.get<Configuration>(`${this.apiUrl}/configuration`);
    }

    // Start the simulation from the backend
    startSimulation(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/simulation/start`, {});
    }

    //Stop the simulation from the backend
    stopSimulation(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/simulation/stop`, {});
    }
}