package com.creh.employees.service;

import com.creh.employees.model.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> findAll();
    Employee save(Employee employee);
    Employee findById(Long id);
}
