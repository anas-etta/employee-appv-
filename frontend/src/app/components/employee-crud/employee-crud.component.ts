import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { EmployeeService, Employee, PaginatedEmployees } from '../../services/employee.service';

@Component({
  selector: 'app-employee-crud',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatPaginatorModule, MatSortModule,
    MatInputModule, MatButtonModule, ReactiveFormsModule
  ],
  templateUrl: './employee-crud.component.html',
  styleUrls: ['./employee-crud.component.css']
})
export class EmployeeCrudComponent implements OnInit {
  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'email', 'actions'];
  dataSource = new MatTableDataSource<Employee>([]);
  totalElements = 0;
  query: string = '';
  selectedEmployee: Employee | null = null;
  isEditing = false;

  employeeForm = new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email])
  });

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadEmployees(0, 10);
  }

  loadEmployees(page: number, size: number): void {
    this.employeeService.getEmployees(page, size).subscribe((data: PaginatedEmployees) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.paginator.length = data.totalElements;
    });
  }

  onPaginateChange(event: any): void {
    const page = event.pageIndex;
    const size = event.pageSize;
    this.loadEmployees(page, size);
  }

  // 🔹 Supprimer un employé
  deleteEmployee(id: number): void {
    if (confirm('Voulez-vous vraiment supprimer cet employé ?')) {
      this.employeeService.deleteEmployee(id).subscribe(() => {
        this.paginator.pageIndex = 0; // Revenir à la première page
        this.loadEmployees(0, this.paginator.pageSize || 10);
      });
    }
  }

  // 🔹 Modifier un employé
  editEmployee(employee: Employee): void {
    this.selectedEmployee = employee;
    this.isEditing = true;
    this.employeeForm.setValue({
      firstName: employee.firstName,
      lastName: employee.lastName,
      email: employee.email
    });
  }

  // 🔹 Ajouter un nouvel employé
  addNewEmployee(): void {
    this.selectedEmployee = null;
    this.isEditing = false;
    this.employeeForm.reset();
  }

  // 🔹 Sauvegarder (ajout ou mise à jour)
  saveEmployee(): void {
    if (this.employeeForm.valid) {
      const employeeData: Employee = this.employeeForm.value as Employee;

      if (this.isEditing && this.selectedEmployee) {
        this.employeeService.updateEmployee(this.selectedEmployee.id, employeeData).subscribe(() => {
          this.paginator.pageIndex = 0; // Revenir à la première page
          this.loadEmployees(0, this.paginator.pageSize || 10);
          this.isEditing = false;
          this.employeeForm.reset();
        });
      } else {
        this.employeeService.createEmployee(employeeData).subscribe(() => {
          this.paginator.pageIndex = 0; // Revenir à la première page
          this.loadEmployees(0, this.paginator.pageSize || 10);
          this.employeeForm.reset();
        });
      }
    }
  }
}
