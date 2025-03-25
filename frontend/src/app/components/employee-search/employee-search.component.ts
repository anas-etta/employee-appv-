import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { EmployeeService, Employee, PaginatedEmployees } from '../../services/employee.service';

@Component({
  selector: 'app-employee-search',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatPaginatorModule, MatSortModule,
    MatInputModule, MatButtonModule, FormsModule
  ],
  templateUrl: './employee-search.component.html',
  styleUrls: ['./employee-search.component.css']
})
export class EmployeeSearchComponent implements OnInit {
  displayedColumns: string[] = ['id', 'firstName', 'lastName', 'email'];
  dataSource = new MatTableDataSource<Employee>([]);
  totalElements = 0;
  query: string = '';

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

  searchEmployees(): void {
    this.paginator.firstPage(); // ✅ Revient à la première page après la recherche
    this.loadSearchResults(0, this.paginator.pageSize || 10);
  }

  loadSearchResults(page: number, size: number): void {
    this.employeeService.searchEmployees(this.query, page, size).subscribe((data: PaginatedEmployees) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.paginator.length = data.totalElements;
    });
  }

  onPaginateChange(event: any): void {
    const page = event.pageIndex;
    const size = event.pageSize;
    if (this.query) {
      this.loadSearchResults(page, size);
    } else {
      this.loadEmployees(page, size);
    }
  }
}
