package com.example.backend_Employee.Controller;

import com.example.backend_Employee.Entities.Employee;
import com.example.backend_Employee.Service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")

public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        Employee savedEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(savedEmployee);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<Employee>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees = employeeService.getAllEmployees(pageable);
        return ResponseEntity.ok(employees);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
            return ResponseEntity.ok(updatedEmployee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<Employee>> searchEmployees(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees = employeeService.searchEmployees(query, pageable);
        return ResponseEntity.ok(employees);
    }
}
