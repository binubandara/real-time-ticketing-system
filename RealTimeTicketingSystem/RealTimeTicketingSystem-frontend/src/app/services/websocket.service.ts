import { Injectable } from "@angular/core";
import { Subject } from 'rxjs';
import * as Stomp from '@stomp/stompjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  //Websocket client using StompJS
  private stompClient!: Stomp.Client;

  private statusSubject = new Subject<any>(); //Updates for system status
  private logsSubject = new Subject<any>(); //For lgs fetched frm the backend
  private connectionStatus = new Subject<boolean>(); //To check the websocket connection status

  // Observables to subscribe to updates
  status$ = this.statusSubject.asObservable(); 
  logs$ = this.logsSubject.asObservable(); 
  connectionStatus$ = this.connectionStatus.asObservable(); 

  //Websocket connection
  connect() {
    this.stompClient = new Stomp.Client({
      brokerURL: `${environment.wsUrl}`, //Websocket endpoint from environment.ts
      connectHeaders: {},
      debug: function(str) {
        console.log('STOMP: ' + str); //Logs for debugging
      },
      reconnectDelay: 4000, //Reconnect every 4 seconds
      heartbeatIncoming: 3000, // Heartbeat interval from server
      heartbeatOutgoing: 3000 // Heartbeat interval to server
    });

    this.stompClient.onConnect = (frame) => {
      console.log('Connected: ' + frame);
      this.connectionStatus.next(true);
      
      // Subscribe and parse status data after successful websocket connection
      this.stompClient.subscribe('/topic/status', (message) => {
        try {
          const data = JSON.parse(message.body);
          this.statusSubject.next(data);
        } catch (error) {
          console.error('Error parsing status message:', error);
        }
      });

      // Subscribe and parse system logs after successful websocket connection
      this.stompClient.subscribe('/topic/logs', (message) => {
        try {
          const data = JSON.parse(message.body);
          this.logsSubject.next(data);
        } catch (error) {
          console.error('Error parsing logs message:', error);
        }
      });

      // Subscribe and parse ticket sales after successful websocket connection
      this.stompClient.subscribe('/topic/ticket-sales', (message) => {
        const salesUpdate = JSON.parse(message.body);
        this.statusSubject.next(salesUpdate);
      });

      // Subscribe and parse ticket releases after successful websocket connection
      this.stompClient.subscribe('/topic/ticket-releases', (message) => {
        const releaseUpdate = JSON.parse(message.body);
        this.logsSubject.next(releaseUpdate);
      });
  
    };

    // Logs broker reported erros
    this.stompClient.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
      this.connectionStatus.next(false); // Notify disconnection
    };

    // Log websocket error if occured
    this.stompClient.onWebSocketError = (event) => {
      console.error('WebSocket error:', event);
      this.connectionStatus.next(false); // Notify disconnection
    };

    //Activate websocket client
    this.stompClient.activate();
  }

  // Diisconnect websocket client
  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.connectionStatus.next(false);
      console.log('Disconnected');
    }
  }
  
}