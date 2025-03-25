import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { KeycloakService } from './keycloak.service';  // Importer le service Keycloak

export interface Employee {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

export interface PaginatedEmployees {
  content: Employee[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // Numéro de page actuel
}

@Injectable({
  providedIn: 'root',
})
export class EmployeeService {
  private baseUrl = 'http://localhost:8080/api/employees';

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService  // Injecter KeycloakService
  ) {}

  // Méthode pour ajouter le token au header
  private getAuthHeaders(): HttpHeaders {
    const token = this.keycloakService.getToken();  // Récupérer le token
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);  // Ajouter le token dans les en-têtes
  }

  getEmployees(page: number, size: number): Observable<PaginatedEmployees> {
    const headers = this.getAuthHeaders();  // Récupérer les en-têtes avec le token
    return this.http.get<PaginatedEmployees>(`${this.baseUrl}?page=${page}&size=${size}`, { headers });
  }

  searchEmployees(query: string, page: number, size: number): Observable<PaginatedEmployees> {
    const headers = this.getAuthHeaders();  // Récupérer les en-têtes avec le token
    return this.http.get<PaginatedEmployees>(`${this.baseUrl}/search?query=${query}&page=${page}&size=${size}`, { headers });
  }

  deleteEmployee(id: number): Observable<void> {
    const headers = this.getAuthHeaders();  // Récupérer les en-têtes avec le token
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { headers });
  }

  createEmployee(employee: Employee): Observable<Employee> {
    const headers = this.getAuthHeaders();  // Récupérer les en-têtes avec le token
    return this.http.post<Employee>(this.baseUrl, employee, { headers });
  }

  updateEmployee(id: number, employee: Employee): Observable<Employee> {
    const headers = this.getAuthHeaders();  // Récupérer les en-têtes avec le token
    return this.http.put<Employee>(`${this.baseUrl}/${id}`, employee, { headers });
  }
}
