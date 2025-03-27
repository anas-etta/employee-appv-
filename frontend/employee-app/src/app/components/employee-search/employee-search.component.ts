import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { EmployeeService, Employee, PaginatedEmployees } from '../../services/employee.service';
import { PageEvent } from '@angular/material/paginator'; 

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
  query = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit(): void {
    this.loadData(0, 10);
  }

  loadData(page: number, size: number, searchQuery = ''): void {
    const loadObservable = searchQuery 
      ? this.employeeService.searchEmployees(searchQuery, page, size)
      : this.employeeService.getEmployees(page, size);

    loadObservable.subscribe((data: PaginatedEmployees) => {
      this.dataSource.data = data.content;
      this.totalElements = data.totalElements;
      this.paginator.length = data.totalElements;
    });
  }

  searchEmployees(): void {
    this.paginator.firstPage();
    this.loadData(0, this.paginator.pageSize || 10, this.query);
  }

  onPaginateChange(event: PageEvent): void {  
    this.loadData(event.pageIndex, event.pageSize, this.query);
  }
}