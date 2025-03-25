package com.example.backend_Employee.Repositories;

import com.example.backend_Employee.Entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Recherche d'employés par prénom avec pagination
    Page<Employee> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    // Recherche d'un employé par email
    Employee findByEmail(String email);

    Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, Pageable pageable);

}
