package com.creh.employees.service;

import com.creh.employees.model.Employee;
import com.creh.employees.repository.EmployeeRepository;
import com.creh.employees.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        employee1 = new Employee(1L, "Alejandro", "Villamizar", "villamizar@gmail.com", 1234567890L, new HashSet<>());
        employee2 = new Employee(2L, "Maria", "Burgos", "mburgos@egmail.com", 1234567891L, new HashSet<>());
    }

    @Test
    public void testFindAll() {
        List<Employee> employees = Arrays.asList(employee1, employee2);
        when(employeeRepository.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.findAll();
        assertEquals(2, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testSave() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        Employee result = employeeService.save(employee1);
        assertEquals(employee1.getId(), result.getId());
        assertEquals(employee1.getName(), result.getName());
        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    public void testFindById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(3L)).thenReturn(Optional.empty());

        Employee result1 = employeeService.findById(1L);
        Employee result2 = employeeService.findById(3L);

        assertEquals(employee1.getId(), result1.getId());
        assertNull(result2);
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findById(3L);
    }
}

