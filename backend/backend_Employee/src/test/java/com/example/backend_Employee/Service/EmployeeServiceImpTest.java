package com.example.backend_Employee.Service;

import com.example.backend_Employee.Entities.Employee;
import com.example.backend_Employee.Repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee(1L, "mock", "test", "mock.test@example.com");
    }

    @Test
    void testCreateEmployee() {
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee createdEmployee = employeeService.createEmployee(employee);

        assertNotNull(createdEmployee);
        assertEquals("mock", createdEmployee.getFirstName());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testGetAllEmployees() {
        Page<Employee> employeePage = new PageImpl<>(List.of(employee));
        when(employeeRepository.findAll(PageRequest.of(0, 10))).thenReturn(employeePage);

        Page<Employee> employees = employeeService.getAllEmployees(PageRequest.of(0, 10));

        assertNotNull(employees);
        assertFalse(employees.isEmpty());
        assertEquals(1, employees.getContent().size());
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Optional<Employee> foundEmployee = employeeService.getEmployeeById(1L);

        assertTrue(foundEmployee.isPresent());
        assertEquals("mock", foundEmployee.get().getFirstName());
    }

    @Test
    void testUpdateEmployee() {
        Employee updatedDetails = new Employee(1L, "mocka", "testa", "mocka.testa@example.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(updatedDetails)).thenReturn(updatedDetails);

        Employee updatedEmployee = employeeService.updateEmployee(1L, updatedDetails);

        assertNotNull(updatedEmployee);
        assertEquals("mocka", updatedEmployee.getFirstName());
    }

    @Test
    void testDeleteEmployee() {
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
