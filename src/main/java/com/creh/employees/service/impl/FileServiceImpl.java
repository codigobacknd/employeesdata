package com.creh.employees.service.impl;

import com.creh.employees.exception.DatabaseOperationException;
import com.creh.employees.exception.FileProcessingException;
import com.creh.employees.model.Employee;
import com.creh.employees.model.Role;
import com.creh.employees.service.EmployeeService;
import com.creh.employees.service.FileService;
import com.creh.employees.service.RoleService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RoleService roleService;

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void uploadFile(MultipartFile file) {
        List<Employee> employees;
        try {
            employees = parseFile(file);
        } catch (IOException e) {
            throw new FileProcessingException("Error processing file", e);
        }

        employees.forEach(employee -> executorService.submit(() -> {
            try {
                Employee savedEmployee = employeeService.save(employee);
                for (Role role : employee.getRoles()) {
                    role.setEmployee(savedEmployee);
                    roleService.save(role);
                }
            } catch (Exception e) {
                throw new DatabaseOperationException("Error saving employee or roles to database", e);
            }
        }));
    }

    private List<Employee> parseFile(MultipartFile file) throws IOException {
        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            return StreamSupport.stream(sheet.spliterator(), false)
                    .skip(1)
                    .map(this::parseRowToEmployee)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new FileProcessingException("Error reading Excel file", e);
        }
    }

    private Employee parseRowToEmployee(Row row) {
        Employee employee = new Employee(1L, "John", "Engineering", "jhon@example.com", 3045461465L, new HashSet<>());
        employee.setName(row.getCell(0).getStringCellValue());
        employee.setLastName(row.getCell(1).getStringCellValue());
        employee.setEmail(row.getCell(2).getStringCellValue());
        employee.setPhone((long) row.getCell(3).getNumericCellValue());

        String roles = row.getCell(4).getStringCellValue();
        Set<Role> rolesList = parseRoles(roles);
        employee.setRoles(rolesList);

        return employee;
    }

    private Set<Role> parseRoles(String roles) {
        Set<Role> rolesList = new HashSet<>();
        String[] rolesArray = roles.split(",");
        for (String roleName : rolesArray) {
            Role role = new Role();
            role.setName(roleName.trim());
            rolesList.add(role);
        }
        return rolesList;
    }
}