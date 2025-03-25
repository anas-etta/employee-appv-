import { Routes } from '@angular/router';
import { EmployeeListComponent } from './components/employee-list/employee-list.component'; // Liste sans recherche
import { EmployeeSearchComponent } from './components/employee-search/employee-search.component'; // Liste avec recherche
import { EmployeeCrudComponent } from './components/employee-crud/employee-crud.component';
import { AuthGuard } from './guards/auth.guard';  // Assurez-vous que vous avez importé le guard

export const routes: Routes = [
  { path: '', redirectTo: '/employees', pathMatch: 'full' },
  { path: 'employees', component: EmployeeListComponent },  // Liste simple
  { path: 'search', component: EmployeeSearchComponent },  // Liste avec recherche
  {
    path: 'employees-crud',
    component: EmployeeCrudComponent,
    canActivate: [AuthGuard],  // Ajout de la protection avec le guard
    data: { roles: ['ROLE_ADMIN'] }  // Seuls les utilisateurs avec ce rôle peuvent accéder
  }
];
