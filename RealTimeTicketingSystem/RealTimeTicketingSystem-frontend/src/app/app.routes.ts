// Routing configuration for the application
import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';

export const routes: Routes = [
  { path: '', component: DashboardComponent }, // Default route pointing to Dashboard
  { path: 'dashboard', component: DashboardComponent }, // Explicit Dashboard route
  { path: '**', redirectTo: '' } // Wildcard route redirects to default
];
