package com.example.batch.Batch;



import com.example.batch.Entities.Employee;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee employee) throws Exception {

        return employee;
    }
}
