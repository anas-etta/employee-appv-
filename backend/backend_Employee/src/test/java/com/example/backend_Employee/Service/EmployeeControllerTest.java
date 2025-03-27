package com.example.backend_Employee.Service;

import com.example.backend_Employee.Controller.EmployeeController;
import com.example.backend_Employee.Entities.Employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private Employee employee;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
        employee = new Employee(1L, "mock", "test", "mock.test@example.com");
    }

    @Test
    void testCreateEmployee() throws Exception {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin1").password("admin123").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"mock\", \"lastName\":\"test\", \"email\":\"mock.test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("mock"));
    }

    @Test
    void testGetAllEmployees() throws Exception {
        when(employeeService.getAllEmployees(any()))
                .thenReturn(new PageImpl<>(List.of(employee), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/employees")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin1").password("admin123").roles("ADMIN"))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("mock"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/api/employees/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin1").password("admin123").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("mock"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        Employee updatedEmployee = new Employee(1L, "mocka", "testa", "mocka.testa@example.com");
        when(employeeService.updateEmployee(eq(1L), any(Employee.class))).thenReturn(updatedEmployee);

        mockMvc.perform(put("/api/employees/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin1").password("admin123").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"mocka\", \"lastName\":\"testa\", \"email\":\"mocka.testa@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("mocka"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin1").password("admin123").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }
}