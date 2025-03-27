package com.example.backend_Employee.mapper;

import com.example.backend_Employee.DTO.EmployeeDTO;
import com.example.backend_Employee.Entities.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeDTO toDTO(Employee employee);
    Employee toEntity(EmployeeDTO employeeDTO);
}
