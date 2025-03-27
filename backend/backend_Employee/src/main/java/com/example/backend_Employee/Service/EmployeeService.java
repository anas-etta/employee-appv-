package com.example.backend_Employee.Service;


import com.example.backend_Employee.Entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmployeeService {
    Employee createEmployee(Employee employee);
    Page<Employee> getAllEmployees(Pageable pageable);
    Optional<Employee> getEmployeeById(Long id);
    Employee updateEmployee(Long id, Employee employee);
    void deleteEmployee(Long id);


    Page<Employee> searchEmployees(String query, Pageable pageable);
}
