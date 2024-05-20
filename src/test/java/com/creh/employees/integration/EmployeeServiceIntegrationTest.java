package com.creh.employees.integration;

import com.creh.employees.model.Employee;
import com.creh.employees.repository.EmployeeRepository;
import com.creh.employees.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    public void setUp() {
        employee1 = new Employee(null, "Alejandro", "Villamizar", "villamizar@gmail.com", 1234567890L, new HashSet<>());
        employee2 = new Employee(null, "Maria", "Burgos", "mburgos@gmail.com", 1234567891L, new HashSet<>());
        employeeRepository.deleteAll();
    }

    @Test
    public void testFindAll() {
        employeeService.save(employee1);
        employeeService.save(employee2);

        List<Employee> employees = employeeService.findAll();
        assertEquals(2, employees.size());
        assertTrue(employees.contains(employee1));
        assertTrue(employees.contains(employee2));
    }

    @Test
    public void testSave() {
        Employee savedEmployee = employeeService.save(employee1);
        assertNotNull(savedEmployee.getId());
        assertEquals(employee1.getName(), savedEmployee.getName());
    }

    @Test
    public void testFindById() {
        Employee savedEmployee = employeeService.save(employee1);
        Employee foundEmployee = employeeService.findById(savedEmployee.getId());
        assertEquals(savedEmployee.getId(), foundEmployee.getId());
        assertEquals(savedEmployee.getName(), foundEmployee.getName());
    }

    @Test
    public void testFindByIdNotFound() {
        Employee foundEmployee = employeeService.findById(999L);
        assertNull(foundEmployee);
    }
}