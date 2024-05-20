package com.creh.employees.controller;

import com.creh.employees.exception.DatabaseOperationException;
import com.creh.employees.exception.FileProcessingException;
import com.creh.employees.model.Employee;
import com.creh.employees.response.Response;
import com.creh.employees.response.ResponseDetail;
import com.creh.employees.service.EmployeeService;
import com.creh.employees.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FileService fileService;

    @GetMapping("/all")
    public ResponseEntity<Response> getAllEmployees() {
        List<Employee> employees = employeeService.findAll();
        Response response = new Response(HttpStatus.OK.value(), "Employees retrieved successfully", employees, Instant.now().toString(), null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<Response> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.uploadFile(file);
            Response response = new Response(HttpStatus.OK.value(), "File loaded successfully", null, Instant.now().toString(), null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response errorResponse = new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null, Instant.now().toString(), new ResponseDetail(e.getClass().getSimpleName(), e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
