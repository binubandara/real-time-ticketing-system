import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import { TicketingService } from '../../services/ticketing.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-configuration-input',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './configuration-input.component.html',
  styleUrl: './configuration-input.component.css'
})
export class ConfigurationInputComponent implements OnInit{
  configForm: FormGroup; // Reactive form to handle user inputs

  constructor(
    private fb: FormBuilder,
    private ticketingService: TicketingService
  ) {

    //Initialize form with validators for each input
    this.configForm = this.fb.group({
      totalTickets: ['', [Validators.required, Validators.min(1)]],
      ticketReleaseRate: ['', [Validators.required, Validators.min(1)]],
      customerRetrievalRate: ['', [Validators.required, Validators.min(1)]],
      maxTicketCapacity: ['', [Validators.required, Validators.min(5)]],
      noOfVendors: ['', [Validators.required, Validators.min(1)]],
      noOfCustomers: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit() {
    this.loadCurrentConfig(); //Load existing configuration 
  }

  //Fetch existing configuration from the backend and inserts to the form
  loadCurrentConfig() {
    this.ticketingService.getConfiguration().subscribe(config => {
      if (config) {
        this.configForm.patchValue(config);
      }
    });
  }

  //Send te user entered configuration data to the backend                                
  onSubmit() {
    if (this.configForm.valid) {
      this.ticketingService.saveConfiguration(this.configForm.value).subscribe({
        next: () => alert('Configuration saved successfully'),
        error: (error) => alert('Error saving configuration')
      });
    }
  }

}
