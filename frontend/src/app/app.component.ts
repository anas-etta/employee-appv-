import { Component, OnInit } from '@angular/core';
import { KeycloakService } from './services/keycloak.service';
import { Router } from '@angular/router';
import { EmployeeListComponent } from './components/employee-list/employee-list.component';
import { EmployeeSearchComponent } from './components/employee-search/employee-search.component';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { EmployeeCrudComponent } from './components/employee-crud/employee-crud.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    EmployeeListComponent, 
    EmployeeSearchComponent, 
    EmployeeCrudComponent, 
    CommonModule, 
    RouterModule
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'employee-app';
  isLoading = true; // Indicateur de chargement pour l'initialisation de Keycloak

  constructor(private keycloakService: KeycloakService, private router: Router) {}

  ngOnInit() {
    // Initialisation de Keycloak
    this.keycloakService.init().then(() => {
      console.log('Keycloak initialized');
      this.isLoading = false;  // Fin du chargement
    }).catch(() => {
      console.error('Keycloak initialization failed');
      this.isLoading = false;
      // Optionnel : rediriger vers la page de login ou afficher un message d'erreur
      this.router.navigate(['/login']);
    });
  }
}
