import { Routes } from '@angular/router';
import { EmployeeListComponent } from './components/employee-list/employee-list.component'; 
import { EmployeeSearchComponent } from './components/employee-search/employee-search.component'; 
import { EmployeeCrudComponent } from './components/employee-crud/employee-crud.component';
import { AuthGuard } from './guards/auth.guard';  

export const routes: Routes = [
  { 
    path: '', 
    redirectTo: '/employees', 
    pathMatch: 'full' 
  },
  { 
    path: 'employees', 
    component: EmployeeListComponent,
    title: 'Liste des employés'
  },
  { 
    path: 'search', 
    component: EmployeeSearchComponent,
    title: 'Recherche d\'employés'
  },
  {
    path: 'employees-crud',
    component: EmployeeCrudComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] },
    title: 'Gestion des employés'
  }
];
